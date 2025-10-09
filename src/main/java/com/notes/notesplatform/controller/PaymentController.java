/*package com.notes.notesplatform.controller;

import com.notes.notesplatform.model.Note;
import com.notes.notesplatform.model.PurchasedNote;
import com.notes.notesplatform.model.User;
import com.notes.notesplatform.repository.NoteRepository;
import com.notes.notesplatform.repository.PurchasedNoteRepository;
import com.notes.notesplatform.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NotesPageController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchasedNoteRepository purchasedNoteRepository;
   


    @GetMapping("/notes")
public String showNotes(@RequestParam(required = false) Long chapterId,
                        Model model,
                        Principal principal) {

    List<Note> notes = new ArrayList<>();
    Map<Long, Boolean> userHasNote = new HashMap<>();

    if (chapterId != null) {
        notes = noteRepository.findByChapterIdAndDeletedFalse(chapterId);

        if (principal != null) {
            
            User user = userRepository.findByEmail(principal.getName()).orElse(null);

            for (Note note : notes) {
                boolean purchased = purchasedNoteRepository.existsByUserAndNote(user, note);
                userHasNote.put(note.getId(), purchased);
            }
        }
    }

    model.addAttribute("notes", notes);
    model.addAttribute("userHasNote", userHasNote);
    return "notes";
}


   @PostMapping("/buyNote")
public String buyNote(@RequestParam Long noteId, Principal principal) {
    User user = userRepository.findByEmail(principal.getName()).orElseThrow();
    Note note = noteRepository.findById(noteId).orElseThrow();

    if (!purchasedNoteRepository.existsByUserAndNote(user, note)) {
        PurchasedNote purchase = new PurchasedNote();
        purchase.setUser(user);
        purchase.setNote(note);
        purchase.setPurchasedAt(LocalDateTime.now());

        purchasedNoteRepository.save(purchase);
    }

    return "redirect:/notes?chapterId=" + note.getChapter().getId(); // redirect back
}



}*



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

    // Inject values from application.properties
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

    /**
     * Creates a Razorpay order on the server.
     * This endpoint is called by JavaScript from the frontend when the user clicks the "Pay" button.
     * @param payload Contains the noteId for which the payment is to be made.
     * @return A JSON response with order details (orderId, key, amount, etc.).
     *
    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> payload, Principal principal) throws RazorpayException {
        // Find the note from the database to get the real price. Never trust the price from the client.
        Long noteId = Long.parseLong(payload.get("noteId").toString());
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Note ID: " + noteId));

        // Amount should be in the smallest currency unit (e.g., paise for INR)
        int amountInPaise = note.getPrice() * 100;

        // Initialize Razorpay client
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        // Prepare order details
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_note_" + note.getId()); // Unique receipt id

        // Create the order
        Order order = razorpayClient.orders.create(orderRequest);

        // Return the order details as a JSON string
        return order.toString();
    }

    /**
     * Verifies the payment signature after the user completes the payment.
     * This is a critical security step.
     * @param payload Contains Razorpay's response (paymentId, orderId, signature) and the noteId.
     * @return A response entity indicating success or failure.
     *
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload, Principal principal) {
        String paymentId = payload.get("razorpay_payment_id");
        String orderId = payload.get("razorpay_order_id");
        String signature = payload.get("razorpay_signature");
        Long noteId = Long.parseLong(payload.get("noteId"));

        try {
            // This is the body that Razorpay expects for signature generation
            String expectedSignatureBody = orderId + "|" + paymentId;

            // Verify the signature
            boolean isValid = Utils.verifyPaymentSignature(expectedSignatureBody, signature, razorpayKeySecret);

            if (isValid) {
                // If signature is valid, proceed with fulfilling the purchase
                User user = userRepository.findByEmail(principal.getName()).orElseThrow();
                Note note = noteRepository.findById(noteId).orElseThrow();

                // Check if the user already owns the note to prevent duplicate purchases
                if (!purchasedNoteRepository.existsByUserAndNote(user, note)) {
                    PurchasedNote purchase = new PurchasedNote();
                    purchase.setUser(user);
                    purchase.setNote(note);
                    purchase.setPurchasedAt(LocalDateTime.now());
                    purchase.setRazorpayPaymentId(paymentId);
                    purchase.setRazorpayOrderId(orderId);
                    purchasedNoteRepository.save(purchase);
                }
                // Return success response
                return ResponseEntity.ok().body(Map.of("status", "success", "message", "Payment verified and note purchased."));
            } else {
                // If signature is invalid
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Payment verification failed."));
            }

        } catch (RazorpayException e) {
            // Handle exceptions from Razorpay
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}*/


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

    // Inject values from application.properties
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
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> payload, Principal principal) throws RazorpayException {
        
        // FINAL FIX: This robustly handles cases where the noteId might arrive as a decimal (e.g., 5.0) 
        // or a whole number (e.g., 5). We convert it to a String, parse it as a Double to accommodate
        // decimals, and then take its long value to safely truncate it to a whole number.
        //String noteIdStr = String.valueOf(payload.get("noteId"));
        //Double noteIdDouble = Double.parseDouble(noteIdStr);
        //Long noteId = noteIdDouble.longValue();

        Object noteIdObj = payload.get("noteId");
    if (!(noteIdObj instanceof Number)) {
        throw new IllegalArgumentException("Note ID must be a number.");
    }
    Long noteId = ((Number) noteIdObj).longValue();


        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Note ID: " + noteId));

        // This line is correct. The amount must be an integer.
        int amountInPaise = note.getPrice().multiply(new java.math.BigDecimal(100)).intValue();

        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_note_" + note.getId());

        Order order = razorpayClient.orders.create(orderRequest);

        return order.toString();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload, Principal principal) {
        String paymentId = payload.get("razorpay_payment_id");
        String orderId = payload.get("razorpay_order_id");
        String signature = payload.get("razorpay_signature");
        Long noteId = Long.parseLong(payload.get("noteId"));

        try {
            String expectedSignatureBody = orderId + "|" + paymentId;
            boolean isValid = Utils.verifySignature(expectedSignatureBody, signature, razorpayKeySecret);

            if (isValid) {
                User user = userRepository.findByEmail(principal.getName()).orElseThrow();
                Note note = noteRepository.findById(noteId).orElseThrow();

                if (!purchasedNoteRepository.existsByUserAndNote(user, note)) {
                    PurchasedNote purchase = new PurchasedNote();
                    purchase.setUser(user);
                    purchase.setNote(note);
                    purchase.setPurchasedAt(LocalDateTime.now());
                    purchase.setRazorpayPaymentId(paymentId);
                    purchase.setRazorpayOrderId(orderId);
                    purchasedNoteRepository.save(purchase);
                }
                return ResponseEntity.ok().body(Map.of("status", "success", "message", "Payment verified and note purchased."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Payment verification failed."));
            }

        } catch (RazorpayException e) {
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}

