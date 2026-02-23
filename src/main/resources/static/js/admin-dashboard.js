const navLinks = document.querySelectorAll('.nav-link');
const sections = document.querySelectorAll('.content-section');

navLinks.forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();

        // 1. Get the ID of the section we want to show
        const targetId = this.getAttribute('data-target');

        // 2. Hide ALL sections
        sections.forEach(section => {
            section.style.display = 'none';
        });

        // 3. Show the specific target section
        document.getElementById(targetId).style.display = 'block';

        // 4. Update "Active" link styling
        navLinks.forEach(l => l.classList.remove('active'));
        this.classList.add('active');
    });
});