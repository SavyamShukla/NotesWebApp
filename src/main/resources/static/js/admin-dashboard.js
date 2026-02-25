const navLinks = document.querySelectorAll('.nav-link');
const sections = document.querySelectorAll('.content-section');

navLinks.forEach(link => {
    link.addEventListener('click', function (e) {
        e.preventDefault();


        const targetId = this.getAttribute('data-target');


        sections.forEach(section => {
            section.style.display = 'none';
        });


        document.getElementById(targetId).style.display = 'block';


        navLinks.forEach(l => l.classList.remove('active'));
        this.classList.add('active');
    });
});