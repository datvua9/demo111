$(document).ready(function() {
    // Khi chọn một đánh giá sao
    $('.rating-system2 input[type="radio"]').change(function() {
        var ratingName = $(this).attr('name');  // Lấy tên của nhóm radio
        var ratingValue = $(this).val();        // Lấy giá trị được chọn

        // Cập nhật giá trị vào input ẩn tương ứng
        $('#' + ratingName).val(ratingValue);
    });

    // Khi chọn nền tảng (platform)
    $('#platform').change(function() {
        $('#platform_value').val($(this).val());
    });

    // Kiểm tra giá trị trước khi submit
    $('form').submit(function(event) {
        var isValid = true;
        $('.rating-system2 input[type="radio"]').each(function() {
            var ratingName = $(this).attr('name');
            if ($('input[name="' + ratingName + '"]:checked').length === 0) {
                alert("Vui lòng đánh giá tất cả các mục.");
                isValid = false;
                return false; // Dừng vòng lặp
            }
        });

        if (!isValid) {
            event.preventDefault(); // Ngăn form submit nếu có lỗi
        }
    });
});
