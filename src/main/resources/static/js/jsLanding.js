window.onload = function () {
    const urlParams = new URLSearchParams(window.location.search);
    const searchParam = urlParams.get("search");
    const pageParam = urlParams.get("page");

    const displayElement = document.getElementById("searchDisplay");

    if (!searchParam) {
        displayElement.innerHTML = "<p>recent blogs with various topics</p>";
    } else {
        displayElement.innerHTML =
            "<p>recent posts with the topic: " + searchParam + "</p>";

        const postElement = document.getElementById("blog");

        if (postElement) {
            postElement.scrollIntoView({ behavior: "smooth", block: "start" });
        }
    }

    if (pageParam) {
        const postElement = document.getElementById("blog");

        if (postElement) {
            postElement.scrollIntoView({ behavior: "smooth", block: "start" });
        }
    }
};
