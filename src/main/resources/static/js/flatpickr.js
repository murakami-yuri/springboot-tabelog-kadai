let maxDate = new Date();
 maxDate = maxDate.setMonth(maxDate.getMonth() + 3);
 
 flatpickr('#visitDateTime', {
   locale: 'ja',
   minDate: 'today',
   maxDate: maxDate,
   enableTime: true,
   onClose: function(selectedDates, dateStr, instance) {
	   const datetime = dateStr.split(" ")
	   document.querySelector("input[name='visitDate']").value = datetime[0];
	   document.querySelector("input[name='visitTime']").value = datetime[1];
   }
 });