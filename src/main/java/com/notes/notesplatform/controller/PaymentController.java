package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.NoteRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;
import com.notes.notesplatform.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchasedNoteRepository purchasedNoteRepository;

    
    @PostMapping("/create_order")
    public String createOrder(@RequestBody Map<String, Object> payload) throws RazorpayException {

        Object noteIdObj = payload.get("noteId");
        if (!(noteIdObj instanceof Number)) {
            throw new IllegalArgumentException("Note ID must be a number.");
        }

        Long noteId = ((Number) noteIdObj).longValue();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Note ID: " + noteId));

        int amountInPaise =
                note.getPrice()
                .multiply(new java.math.BigDecimal(100))
                .intValue();

        RazorpayClient razorpayClient =
                new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_note_" + note.getId());

        Order order = razorpayClient.orders.create(orderRequest);

        return order.toString();
    }

    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload,
                                           Principal principal) {

        String paymentId = payload.get("razorpay_payment_id");
        String orderId = payload.get("razorpay_order_id");
        String signature = payload.get("razorpay_signature");
        Long noteId = Long.parseLong(payload.get("noteId"));

        try {

            String expectedSignatureBody = orderId + "|" + paymentId;

            boolean isValid = Utils.verifySignature(
                    expectedSignatureBody,
                    signature,
                    razorpayKeySecret
            );

            if (isValid) {

                User user = userRepository
                        .findByEmail(principal.getName())
                        .orElseThrow();

                Note note = noteRepository
                        .findById(noteId)
                        .orElseThrow();

                
                boolean alreadyPurchased =
                        purchasedNoteRepository
                        .existsByUserIdAndNoteId(
                                user.getId(),
                                note.getId()
                        );

                if (!alreadyPurchased) {

                    PurchasedNote purchase = new PurchasedNote();
                    purchase.setUser(user);
                    purchase.setNote(note);
                    purchase.setPurchasedAt(LocalDateTime.now());
                    purchase.setRazorpayPaymentId(paymentId);
                    purchase.setRazorpayOrderId(orderId);
                    purchase.setPurchased(true);

                    purchasedNoteRepository.save(purchase);
                }

                return ResponseEntity.ok().body(
                        Map.of(
                                "status", "success",
                                "message", "Payment verified and note purchased."
                        )
                );

            } else {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "status", "error",
                                "message", "Payment verification failed."
                        )
                );
            }

        } catch (RazorpayException e) {

            return ResponseEntity.status(500).body(
                    Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    )
            );
        }
    }
}

