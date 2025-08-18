document.addEventListener("DOMContentLoaded", () => {
    const sendOtpBtn = document.getElementById("send-otp-btn");
    const otpSection = document.getElementById("otp-section");
    const verifyOtpBtn = document.getElementById("verify-otp-btn");
    const registerForm = document.getElementById("step-2");

    function showPopup(steps) {
        const popup = document.getElementById("status-popup");
        const text = document.getElementById("popup-text");
        const spinner = popup.querySelector(".spinner");
        let i = 0;
        popup.classList.remove("hidden");

        function nextStep() {
            if (i >= steps.length) {
                popup.classList.add("hidden");
                return;
            }
            text.textContent = steps[i].text;
            spinner.style.display = steps[i].done ? "none" : "block";
            setTimeout(() => {
                i++;
                nextStep();
            }, steps[i].duration);
        }
        nextStep();
    }

   // Send OTP
sendOtpBtn.addEventListener("click", () => {
    const email = document.getElementById("email").value;

    if (!email) {
        showPopup([{ text: "Please enter an email", duration: 2000, done: true }]);
        return;
    }

    sendOtpBtn.disabled = true;
    const originalText = sendOtpBtn.textContent;
    sendOtpBtn.textContent = "Processing...";

    // Show verifying email popup first
    showPopup([{ text: "Verifying Email...", duration: 1500 }]);

    fetch("/register/send-otp?email=" + encodeURIComponent(email), { method: "POST" })
        .then(res => res.text())
        .then(response => {
            sendOtpBtn.disabled = false;
            sendOtpBtn.textContent = originalText;

            if (response === "exists") {
                showPopup([{ text: "Email already registered!", duration: 2000, done: true }]);
            } else if (response === "sent") {
                // Only proceed if backend confirms OTP was sent
                showPopup([
                    { text: "Generating OTP...", duration: 1500 },
                    { text: "Sending OTP...", duration: 2000 },
                    { text: "OTP Sent!", duration: 1500, done: true }
                ]);
                otpSection.classList.remove("hidden");
            } else {
                showPopup([{ text: "Error sending OTP", duration: 2000, done: true }]);
            }
        })
        .catch(() => {
            sendOtpBtn.disabled = false;
            sendOtpBtn.textContent = originalText;
            showPopup([{ text: "Network error", duration: 2000, done: true }]);
        });
});

    // Verify OTP
    verifyOtpBtn.addEventListener("click", () => {
        const email = document.getElementById("email").value;
        const otp = document.getElementById("otp").value;

        if (!otp) {
            showPopup([{ text: "Please enter OTP", duration: 2000, done: true }]);
            return;
        }

        showPopup([
            { text: "Verifying OTP...", duration: 2000 },
           
        ]);

        fetch("/register/verify-otp?email=" + encodeURIComponent(email) + "&otp=" + encodeURIComponent(otp), { method: "POST" })
            .then(res => res.text())
            .then(response => {
                if (response === "verified") {
                showPopup([ { text: "OTP Verified!", duration: 1500, done: true }]);
                 document.getElementById("step-1").classList.remove("active");
                 document.getElementById("step-1").classList.add("hidden");

                 document.getElementById("step-2").classList.remove("hidden");
                 document.getElementById("step-2").classList.add("active");

                 document.getElementById("verified-email").value = email;
                 }
          else {
                    showPopup([{ text: "Invalid OTP", duration: 2000, done: true }]);
                }
            })
            .catch(() => {
                showPopup([{ text: "Network error", duration: 2000, done: true }]);
            });
    });

    // Register user
    registerForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const formData = new FormData(registerForm);

        showPopup([{ text: "Registering...", duration: 2000 }, { text: "Registered successfully!", duration: 1500, done: true }]);

        fetch("/register/complete", { method: "POST", body: formData })
            .then(res => res.text())
            .then(response => {
                if (response === "success") {
                    setTimeout(() => window.location.href = "/login", 3500);
                } else {
                    showPopup([{ text: "Registration failed", duration: 2000, done: true }]);
                }
            })
            .catch(() => {
                showPopup([{ text: "Network error", duration: 2000, done: true }]);
            });
    });
});