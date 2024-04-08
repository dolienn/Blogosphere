document.addEventListener('DOMContentLoaded', function () {
    var navbarToggle = document.querySelector('.navbar-toggle');
    var navbarNav = document.querySelector('.navbar-nav');

    navbarToggle.addEventListener('click', function () {
        navbarNav.style.maxHeight = navbarNav.style.maxHeight ? null : navbarNav.scrollHeight + 'px';
    });
});