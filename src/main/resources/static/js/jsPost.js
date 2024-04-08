window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const pageParam = urlParams.get('page');

    if(pageParam) {
        const reviews = document.getElementById('reviews');

        if (reviews) {
            reviews.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }
}

document.addEventListener("DOMContentLoaded", function() {
    var menuBtns = document.querySelectorAll(".comment-options-button");
    var menuOptions = document.querySelectorAll(".comment-options");

    const commentInput = document.getElementById('comment');
    const submitButton = document.getElementById('submitButton');

    var editButtons = document.querySelectorAll('.edit-button');
    var cancelButtons = document.querySelectorAll('.cancel');

    var editComments = document.querySelectorAll('.edit-comment');
    var editSubmitButtons = document.querySelectorAll('.edit-submit');



    menuOptions.forEach(function(option) {
        option.style.display = "none";
    });

    menuBtns.forEach(function(btn, index) {
        btn.addEventListener("click", function() {
            var options = menuOptions[index];

            if (options.style.display === "block") {
                options.style.display = "none";
            } else {
                options.style.display = "block";
            }
        });
    });

    commentInput.addEventListener('input', function () {
        if (commentInput.value.trim() !== '') {
            submitButton.removeAttribute('disabled');
            submitButton.style.backgroundColor = '#2b2b2b';
            submitButton.style.cursor = 'pointer';
        } else {
            submitButton.setAttribute('disabled', 'disabled');
            submitButton.style.backgroundColor = '#3c393d';
            submitButton.style.cursor = 'not-allowed';
        }
    });

    editButtons.forEach(function(button, index) {
        button.addEventListener('click', function() {
            var reviewContainer = this.closest('.review');
            var commentText = reviewContainer.querySelector('.comment-text');
            var reviewEditor = reviewContainer.querySelector('.reviewEditor');
            var textarea = reviewEditor.querySelector('textarea');

            textarea.value = commentText.textContent.trim();

            textarea.style.height = commentText.scrollHeight + 'px';
            
            commentText.style.display = 'none';
            reviewEditor.style.display = 'block';

            var options = menuOptions[index];

            if (options.style.display === "block") {
                options.style.display = "none";
            }
        });
    });

    cancelButtons.forEach(function(button, index) {
        button.addEventListener('click', function() {
            var reviewContainer = this.closest('.review');
            var commentText = reviewContainer.querySelector('.comment-text');
            var reviewEditor = reviewContainer.querySelector('.reviewEditor');
            
            reviewEditor.style.display = 'none';
            commentText.style.display = 'block';

            var options = menuOptions[index];

            if (options.style.display === "block") {
                options.style.display = "none";
            }

            textarea.value = '';
        });
    });


    editSubmitButtons.forEach((button, index) => {
        editComments[index].addEventListener('input', function() {
            var reviewContainer = this.closest('.review');
            var commentText = reviewContainer.querySelector('.comment-text');
            console.log(commentText.textContent);
    
            if (editComments[index].value.trim() !== '' && editComments[index].value.trim() !== commentText.textContent.trim()) {
                button.removeAttribute('disabled');
                button.style.backgroundColor = '#2b2b2b';
                button.style.cursor = 'pointer';
            } else {
                button.setAttribute('disabled', 'disabled');
                button.style.backgroundColor = '#3c393d';
                button.style.cursor = 'not-allowed';
            }
        });
    });
    
});