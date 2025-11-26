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
	    
	// 날짜 시작
	$('#applyDatePickerStart input').datepicker({
	    format: "yyyy-mm-dd",
	    autoclose: true,
	    todayHighlight: true
	}).on('changeDate', function(e) {

    $('#applyDatePickerEnd input').datepicker('setStartDate', e.date);

    const endDate = $('#applyDatePickerEnd input').datepicker('getDate');
    if (endDate && endDate < e.date) {
        $('#applyDatePickerEnd input').datepicker('setDate', e.date);
    }
});
	// 날짜 종료
	$('#applyDatePickerEnd input').datepicker({
	    format: "yyyy-mm-dd",
	    autoclose: true,
	    todayHighlight: true
	}).on('changeDate', function(e) {

    $('#applyDatePickerStart input').datepicker('setEndDate', e.date);

    const startDate = $('#applyDatePickerStart input').datepicker('getDate');
    if (startDate && startDate > e.date) {
        $('#applyDatePickerStart input').datepicker('setDate', e.date);
    }
});

    // 아이콘 클릭 → input focus → 달력 열림
    $('#icon-calendar').on('click', function () {
        $('#applyDate').focus();
    });
    
	// 아이콘 클릭
	$('#icon-calendar-start').on('click', function() {
	    $('#applyDateStart').focus();
	});
	
	$('#icon-calendar-end').on('click', function() {
	    $('#applyDateEnd').focus();
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