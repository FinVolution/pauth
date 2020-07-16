$(function() {
    function refreshToken() {
        $.ajax({
            url: '/refreshToken',
            type: 'GET',
            success: function (data) {
            },
            error: function (data) {
            }
        });
    }
    refreshToken();
    setInterval(refreshToken, 5 * 60 * 60 * 1000);
});
