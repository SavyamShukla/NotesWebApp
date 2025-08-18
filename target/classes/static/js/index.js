document.addEventListener("DOMContentLoaded", () => {
    console.log("Index page loaded.");
});

// Smooth scroll for internal links
document.querySelectorAll('.navbar nav a').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        if (this.getAttribute('href').startsWith("#")) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        }
    });
});