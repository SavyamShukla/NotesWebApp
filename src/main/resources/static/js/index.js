

/*
document.addEventListener("DOMContentLoaded", () => {
    console.log("Index page loaded.");

    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');


    hamburger.addEventListener('click', () => {
        navLinks.classList.toggle('active');
    });
});


document.querySelectorAll('.nav-links a').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        if (this.getAttribute('href').startsWith("#")) {
            e.preventDefault();


            if (document.querySelector('.nav-links').classList.contains('active')) {
                document.querySelector('.nav-links').classList.remove('active');
            }

            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        }
    });
});*/

document.addEventListener("DOMContentLoaded", () => {
    const mobileBtn = document.getElementById('mobileProfileBtn');
    const mobileMenu = document.getElementById('mobileDropdown');

    // Toggle mobile dropdown
    if (mobileBtn) {
        mobileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            mobileMenu.classList.toggle('active');
        });
    }

    // Smooth scroll and auto-close menu
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            
            // Close menu if open
            if (mobileMenu && mobileMenu.classList.contains('active')) {
                mobileMenu.classList.remove('active');
            }

            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', () => {
        if (mobileMenu && mobileMenu.classList.contains('active')) {
            mobileMenu.classList.remove('active');
        }
    });
});