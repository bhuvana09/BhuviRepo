function validateOnSubmit(insForm) {
	var valid = "";

	valid += validateFirstname(insForm.name);
	valid += validateSsn(insForm.SSN);
	valid += validateAge(insForm.age);
	valid += validateLicenseno(insForm.licenseNumber);
	valid += validateModel(insForm.model);
	valid += validateMake(insForm.make);
	valid += validateGender(insForm.gender);
	valid += validateAccident(insForm.noOfAccidents);
	valid += validateYear(insForm.year);

	if (valid != "") {
		alert("All the Fields need some correction:\n" + valid);
		return false;
	}

	alert("All fields are filled correctly");
	return true;
}

function validateFirstname(field) {
	var error = "";

	var illegalChars = /[^a-zA-Z\s\.]/; // allow only alphabetics

	if (field.value == "") {
		field.style.background = 'LightSlateGray ';
		error = "You didn't enter First Name.\n";
	} else if (illegalChars.test(field.value)) {
		field.style.color = 'red';
		error = "The First Name contains illegal characters.\n";
	} else {
		field.style.background = 'White';
	}

	return error;
}
function validateEmpty(field) {
	var error = "";

	if (field.value.length == 0) {
		field.style.background = 'LightSlateGray ';
		error = "The required field has not been filled in.\n"
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateSsn(field) {
	var error = "";
	var illegalChars = /[^a-zA-Z0-9\s\.]/; // allow alphabetics,numbers

	if (field.value == "") {
		field.style.background = 'LightSlateGray ';
		error = "You didn't enter a Social Security Number .\n";
	} else if (field.value.length < 10) {
		field.style.color = 'red';
		error = "The no of characters should be 10.\n";
	} else if (illegalChars.test(field.value)) {
		field.style.color = 'red';
		error = "The Social Security Number contains illegal characters.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateAge(field) {
	var error = "";
	var illegalChars = /[^0-9]/; // allow only numbers    

	if (field.value == "") {
		error = "You didn't enter Age.\n";
		field.style.background = 'LightSlateGray';
	} else if (illegalChars.test(field.value)) {
		field.style.color = 'red';
		error = "The Age contains illegal characters.\n";
	} else {
		field.style.background = 'white';
	}
	return error;
}

function validateLicenseno(field) {
	var error = "";
	var illegalChars = /[^a-zA-Z0-9\s\.]/; // allow alphabetics,numbers

	if (field.value == "") {
		field.style.background = 'LightSlateGray';
		error = "You didn't enter a Driving License No.\n";
	} else if (illegalChars.test(field.value)) {
		field.style.color = 'red';
		error = "The Driving License No contains illegal characters.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateRegno(field) {
	var error = "";
	var illegalChars = /[^a-zA-Z0-9\s\.]/; // allow alphabetics,numbers

	if (field.value == "") {
		field.style.background = 'LightSlateGray';
		error = "You didn't enter a Registration Number.\n";
	} else if (illegalChars.test(field.value)) {
		field.style.color = 'red';
		error = "The Registration No contains illegal characters.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateModel(field) {
	var error = "";
	var illegalChars = /[^a-zA-Z0-9\s\.]/; // allow alphabetics,numbers

	if (field.value == "") {
		field.style.background = 'LightSlateGray';
		error = "You didn't enter a model.\n";
	} else if (illegalChars.test(field.value)) {
		field.style.color = 'red';
		error = "The model field contains illegal characters.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateMake(field) {
	var error = "";
	var illegalChars = /[^a-zA-Z0-9\s\.]/; // allow alphabetics,numbers

	if (field.value == "") {
		field.style.background = 'LightSlateGray';
		error = "You didn't enter a make field.\n";
	} else if (illegalChars.test(field.value)) {
		field.style.color = 'red';
		error = "The make field contains illegal characters.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateGender(field) {
	var error = "";
	if (field.value == "") {
		field.style.background = 'LightSlateGray';
		error = "You didn't select Gender  field.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateAccident(field) {
	var error = "";
	if (field.selectedIndex == "") {
		field.style.background = 'LightSlateGray';
		error = "You didn't select No. of Accidents incurred field.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}

function validateYear(field) {
	var error = "";
	if (field.selectedIndex == "") {
		field.style.background = 'LightSlateGray';
		error = "You didn't select Year of Reg. field.\n";
	} else {
		field.style.background = 'White';
	}
	return error;
}