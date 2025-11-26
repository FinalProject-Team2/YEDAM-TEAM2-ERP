// teamExportUtil.js



$(function(){
        $('.datepicker').datepicker({
            format: "yyyy-mm-dd",
            autoclose: true,
            todayHighlight: true
        });
    });
    
    
    $('#datePickerGroup').datepicker({
        format: 'yyyy-mm-dd',
        autoclose: true
    });

$('#applyDatePicker input').datepicker({
        format: "yyyy-mm-dd",
        autoclose: true,
        todayHighlight: true
    });

    // 아이콘 클릭 → input focus → 달력 열림
    $('#icon-calendar').on('click', function () {
        $('#applyDate').focus();
    });
    
    
    // 1. Toast UI Grid 테마
tui.Grid.applyTheme('default', {
  cell: {
    normal: {
      border: '#dedede',
      background: '#ffffff',
      showVerticalBorder: true
    },
    header: {
      border: '#dedede',
      background: '#f5f5f5',
      showVerticalBorder: true
    },
    // ✅ rowHeader는 기본 흰색으로 두기 (헤더/바디 공통)
    rowHeader: {
      border: '#dedede',
      background: '#ffffff',
      showVerticalBorder: true
    },
    editable: {
      background: '#FFFDF0',
      text: '#000'
    },
    selectedHeader: {
      background: '#f5f5f5'
    },
    selected: {
      background: '#ffffff'
    }
  }
});