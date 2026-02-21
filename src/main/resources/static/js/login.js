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

    

       async function sendOtpRequest(url, email, buttonId) {
    const btn = document.getElementById(buttonId);
    showPopup("Verifying email...");

    try {
        await new Promise(resolve => setTimeout(resolve, 500));
        popupText.innerText = "Sending OTP...";

        const res = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `email=${encodeURIComponent(email)}`, 
            redirect: "follow"
        });

        const responseText = await res.text();

        if (!res.ok) {
            // If it's a cooldown error (429), the message is "Please wait X seconds..."
            throw new Error(responseText);
        }

        popupText.innerText = "OTP sent!";
        startCooldownTimer(btn, 60); // Start 60s cooldown on success
        hidePopup(1500);

    } catch (err) {
        popupText.innerText = err.message;
        
        // Check if the error message contains a number to sync the button timer
        const match = err.message.match(/\d+/);
        if (match) {
            startCooldownTimer(btn, parseInt(match[0]));
        }
        
        hidePopup(3000);
    }
}

function startCooldownTimer(button, seconds) {
    if (!button) return;
    
    button.disabled = true;
    const originalText = button.innerText;
    
    const interval = setInterval(() => {
        seconds--;
        button.innerText = `Wait ${seconds}s`;
        
        if (seconds <= 0) {
            clearInterval(interval);
            button.disabled = false;
            button.innerText = originalText;
        }
    }, 1000);
}

    // Login OTP handler
    document.getElementById("send-otp").addEventListener("click", () => {
        const email = document.querySelector('#otp-login input[name="email"]').value.trim();
        if (!email) {
            alert("Please enter your email first.");
            return;
        }
        sendOtpRequest("/send-login-otp", email, "send-otp" );
    });

    // Forgot Password OTP handler
    document.getElementById("forgot-send-otp").addEventListener("click", () => { 
        const email = document.querySelector('#forgot-form input[name="email"]').value.trim();
        if (!email) {
            alert("Please enter your email first.");
            return;
        }
        sendOtpRequest("/forgot-password/send-otp", email, "forgot-send-otp");
    });
});