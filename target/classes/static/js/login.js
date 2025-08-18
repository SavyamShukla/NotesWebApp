document.addEventListener("DOMContentLoaded", () => {
    const passwordBtn = document.getElementById("password-btn");
    const otpBtn = document.getElementById("otp-btn");
    const passwordLogin = document.getElementById("password-login");
    const otpLogin = document.getElementById("otp-login");
    const forgotPasswordBtn = document.getElementById("forgot-password");
    const forgotModal = document.getElementById("forgot-modal");
    const backBtn = document.getElementById("back-btn");
    const togglePasswordIcons = document.querySelectorAll(".toggle-password");

    // Toggle between Password and OTP login
    passwordBtn.addEventListener("click", () => {
        passwordBtn.classList.add("active");
        otpBtn.classList.remove("active");
        passwordLogin.classList.remove("hidden");
        otpLogin.classList.add("hidden");
    });

    otpBtn.addEventListener("click", () => {
        otpBtn.classList.add("active");
        passwordBtn.classList.remove("active");
        otpLogin.classList.remove("hidden");
        passwordLogin.classList.add("hidden");
    });

    // Toggle password visibility
    togglePasswordIcons.forEach(icon => {
        icon.addEventListener("click", () => {
            const input = icon.previousElementSibling;
            input.type = input.type === "password" ? "text" : "password";
        });
    });

    // Forgot password modal
    forgotPasswordBtn.addEventListener("click", () => {
        forgotModal.classList.remove("hidden");
        document.querySelector(".login-container").style.display = "none"; // hide login form
    });

    backBtn.addEventListener("click", () => {
        forgotModal.classList.add("hidden");
        document.querySelector(".login-container").style.display = "block"; // show login form
    });

    // Status popup
    const statusPopup = document.getElementById("status-popup");
    const popupText = document.getElementById("popup-text");

    function showPopup(message) {
        popupText.innerText = message;
        statusPopup.classList.remove("hidden");
    }

    function hidePopup(delay = 1000) {
        setTimeout(() => statusPopup.classList.add("hidden"), delay);
    }

    async function sendOtpRequest(url, email) {
        showPopup("Verifying email...");
        try {
            await new Promise(resolve => setTimeout(resolve, 500));
            popupText.innerText = "Sending OTP...";

            const res = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `email=${encodeURIComponent(email)}`, redirect: "follow"
            });

            if(!res.ok){
                throw new Error(`Server returned ${res.status}`);
            }
            popupText.innerText = "OTP sent!";
            hidePopup(1500);
        } catch (err) {
            popupText.innerText = "Error sending OTP.";
            hidePopup(2000);
        }
    }

    // Login OTP handler
    document.getElementById("send-otp").addEventListener("click", () => {
        const email = document.querySelector('#otp-login input[name="email"]').value.trim();
        if (!email) {
            alert("Please enter your email first.");
            return;
        }
        sendOtpRequest("/send-login-otp", email);
    });

    // Forgot Password OTP handler
    document.getElementById("forgot-send-otp").addEventListener("click", () => { 
        const email = document.querySelector('#forgot-form input[name="email"]').value.trim();
        if (!email) {
            alert("Please enter your email first.");
            return;
        }
        sendOtpRequest("/forgot-password/send-otp", email);
    });
});