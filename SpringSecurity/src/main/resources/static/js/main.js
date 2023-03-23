function isReload() {
    const pageAccessedByReload = ((window.performance.navigation && window.performance.navigation.type === 1) || window.performance
            .getEntriesByType('navigation')
            .map((nav) => nav.type)
            .includes('reload'));

    return pageAccessedByReload;
}

// //check email is valid or not
// $('#email').focusout(function ()
// {
//     if($('#email').val().match(regex))
//     {
//         document.getElementById('email').className = '';
//         $('#email').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);
//         $('#notifyEmail').addClass("text-danger-emphasis");
//         $('#notifyEmail').html("This field can not be empty");
//     }
//     else
//     {
//         document.getElementById('email').className = '';
//         document.getElementById('notifyEmail').className = '';
//         $('#email').addClass("form-control");
//         $('#notifyEmail').addClass("form-text");
//         $('#notifyEmail').empty();
//     }
// })

// Check if email is exist or not
$(document).ready(function ()
{
    $("#email").on('focusin keyup', function()
    {
        let email = $('#email').val();
        const regex =   /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
        if(email.match(regex))
        {
            $.ajax(
                {
                    url:'/api/v1/auth/checkEmail',
                    type: 'GET',
                    data:
                        {
                            'email': email,
                        },
                    error: function ()
                    {

                    },
                    success: function (data)
                    {

                        const dataPoints = JSON.parse(data);
                        console.log(dataPoints);
                        if (dataPoints.email == email)
                        {
                            console.log('have');
                            $('#notifyEmail').className='';
                            $('#notifyEmail').addClass("form-text text-danger-emphasis");
                            $('#notifyEmail').html('Email has already existed');
                        }
                        else
                        {
                            console.log('not have');
                            $('#notifyEmail').className='';
                            $('#notifyEmail').addClass("form-text text-success-emphasis");
                            $('#notifyEmail').html('Valid Email');
                        }
                    }
                }
            )
        }
        else
        {
            $('#notifyEmail').className='';
            $('#notifyEmail').addClass("form-text text-danger-emphasis");
            $('#notifyEmail').html('Invalid Email');
        }
    })


})

//jquery check password and confirm password are the same
$('#password2,#password').on('keyup', function () {
    if ($('#password').val() == $('#password2').val()) {
        $("#notifyPassword").html("<i class=\"fa-regular fa-circle-check\"></i> Password and Confirm password are matching").css('color', 'green');
        $('#register').prop("disabled", false);
    } else {
        $('#register').prop("disabled", true);
        $("#notifyPassword").html("<i class=\"fa-solid fa-xmark\"></i> Password and Confirm password are not matching").css('color', 'red');
    }
})

//Indicator how Strong password
let upperChar = /[A-Z]/, //Leter A-Z
    lowerChar = /[a-z]/, // Letter a-z
    number = /[0-9]/,//number 0 - 9
    specialChar = /[!,@#$%^&*?()-+=~_';:".<>]/; //special character

$('#password').keyup(function () {

    let input = document.getElementById("password").value;
    let indicator = 0;

    if (input.match(upperChar)) {
        document.getElementById('upperIcon').className = '';
        $('#upperIcon').addClass("fa-regular fa-circle-check ")
        indicator += 1;
    } else {
        document.getElementById('upperIcon').className = '';
        $('#upperIcon').addClass("fa-solid fa-circle")
    }

    if (input.match(lowerChar)) {
        document.getElementById('lowIcon').className = '';
        $('#lowIcon').addClass("fa-regular fa-circle-check ")
        indicator += 1;
    } else {
        document.getElementById('lowIcon').className = '';
        $('#lowIcon').addClass("fa-solid fa-circle")
    }

    if (input.match(number)) {
        document.getElementById('numberIcon').className = '';
        $('#numberIcon').addClass("fa-regular fa-circle-check")
        indicator += 1;
    } else {
        document.getElementById('numberIcon').className = '';
        $('#numberIcon').addClass("fa-solid fa-circle")
    }

    if (input.match(specialChar)) {
        document.getElementById('specialIcon').className = '';
        $('#specialIcon').addClass("fa-regular fa-circle-check")
        indicator += 1;
    } else {
        document.getElementById('specialIcon').className = '';
        $('#specialIcon').addClass("fa-solid fa-circle")
    }

    if (input.length >= 8) {
        document.getElementById('lengthIcon').className = '';
        $('#lengthIcon').addClass("fa-regular fa-circle-check")
        indicator += 1;
    } else {
        document.getElementById('lengthIcon').className = '';
        $('#lengthIcon').addClass("fa-solid fa-circle")
    }

    let rmvCls = document.getElementById('password'); // for change class password
    let rmvCls1 = document.getElementById("progressBar");// for change class progress bar
    let rmvCls2 = document.getElementById("notifyStrength");// for change class notify strength password

    switch (indicator) {
        case 0:
            //Remove all class name in tag to make change
            rmvCls.className = '';
            rmvCls1.className = '';
            rmvCls2.className = '';
            //Add class to change color base on how strength
            $('#password').addClass(["form-control"]);

            //Change color and add 20% base on what condition pass
            $('#progressBar').addClass(["progress-bar"]);
            $('#progressBar').css("width", "0%");

            // CHanged for aria-valuenow
            $('#bar').attr("aria-valuenow", 0);

            // make check for the conditional has passed
            $('#notifyStrength').html("");
            break;

        case 1:
            //Remove all class name in tag to make change
            rmvCls.className = '';
            rmvCls1.className = '';
            rmvCls2.className = '';
            //Add class to change color base on how strength
            $('#password').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);

            //Change color and add 20% base on what condition pass
            $('#progressBar').addClass(["progress-bar", "bg-danger"]);
            $('#progressBar').css("width", "20%");

            // CHanged for aria-valuenow
            $('#bar').attr("aria-valuenow", 20);

            // Tick the conditional has pass
            $('#notifyStrength').html("<i class=\"fa-solid fa-circle-info\"></i> Password is weak");
            $('#notifyStrength').addClass("text-danger");
            break;

        case 2:
            //Remove all class name in tag to make change
            rmvCls.className = '';
            rmvCls1.className = '';
            rmvCls2.className = '';
            //Add class to change color base on how strength
            $('#password').addClass(["form-control", "bg-warning", "border border-3", "border-warning", "text-secondary", "bg-opacity-10"]);

            //Change color and add 20% base on what condition pass
            $('#progressBar').addClass(["progress-bar", "bg-warning"]);
            $('#progressBar').css("width", "40%");

            // CHanged for aria-valuenow
            $('#bar').attr("aria-valuenow", 40);

            // Tick the conditional has pass
            $('#notifyStrength').html("<i class=\"fa-solid fa-circle-info\"></i> Password is medium");
            $('#notifyStrength').addClass("text-warning");
            break;

        case 3:
            //Remove all class name in tag to make change
            rmvCls.className = '';
            rmvCls1.className = '';
            rmvCls2.className = '';
            //Add class to change color base on how strength
            $('#password').addClass(["form-control", "bg-warning", "border border-3", "border-warning", "text-secondary", "bg-opacity-10"]);

            //Change color and add 20% base on what condition pass
            $('#progressBar').addClass(["progress-bar", "bg-warning"]);
            $('#progressBar').css("width", "60%");

            // CHanged for aria-valuenow
            $('#bar').attr("aria-valuenow", 60);

            // Tick the conditional has pass
            $('#notifyStrength').html("<i class=\"fa-solid fa-circle-info\"></i> Password is medium");
            $('#notifyStrength').addClass("text-warning");
            break;

        case 4:
            //Remove all class name in tag to make change
            rmvCls.className = '';
            rmvCls1.className = '';
            rmvCls2.className = '';
            //Add class to change color base on how strength
            $('#password').addClass(["form-control", "bg-warning", "border border-3", "border-warning", "text-secondary", "bg-opacity-10"]);

            //Change color and add 20% base on what condition pass
            $('#progressBar').addClass(["progress-bar", "bg-warning"]);
            $('#progressBar').css("width", "80%");

            // CHanged for aria-valuenow
            $('#bar').attr("aria-valuenow", 80);

            // Tick the conditional has pass
            $('#notifyStrength').html("<i class=\"fa-solid fa-circle-info\"></i> Password is medium");
            $('#notifyStrength').addClass("text-warning");
            break;

        case 5:
            //Remove all class name in tag to make change
            rmvCls.className = '';
            rmvCls1.className = '';
            rmvCls2.className = '';
            //Add class to change color base on how strength
            $('#password').addClass(["form-control", "bg-success", "border border-3", "border-success", "text-secondary", "bg-opacity-10"]);

            //Change color and add 20% base on what condition pass
            $('#progressBar').addClass(["progress-bar", "bg-success"]);
            $('#progressBar').css("width", "100%");

            // CHanged for aria-valuenow
            $('#bar').attr("aria-valuenow", 100);

            // Tick the conditional has pass
            $('#notifyStrength').html("<i class=\"fa-solid fa-circle-info\"></i> Password is strong");
            $('#notifyStrength').addClass("text-success");
            break;
    }
})

//Check the required is empty or not

$(document).ready(function () {
    $('#email').on('focusout keyup', function () {
        if ($("#email").val().length == 0) {
            document.getElementById('email').className = '';
            $('#email').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);
            $('#notifyEmail').addClass("text-danger-emphasis");
            $('#notifyEmail').html("This field can not be empty");
        } else {
            document.getElementById('email').className = '';
            document.getElementById('notifyEmail').className = '';
            $('#email').addClass("form-control");
            $('#notifyEmail').addClass("form-text");
            $('#notifyEmail').empty();
        }
    })

    $('#password').on('focusout keyup', function () {
        if ($("#password").val().length == 0) {
            document.getElementById('password').className = '';
            $('#password').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);
            $('#notifyPasswordCheck').addClass("text-danger-emphasis");
            $('#notifyPasswordCheck').html("This field can not be empty");
        } else {
            document.getElementById('notifyPasswordCheck').className = '';
            $('#notifyPasswordCheck').addClass("form-text");
            $('#notifyPasswordCheck').empty();
        }
    })

    $('#password2').on('focusout keyup', function () {
        if ($("#password2").val().length == 0) {
            document.getElementById('password2').className = '';
            $('#password2').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);
        } else {
            document.getElementById('password2').className = '';
            $('#password2').addClass("form-control");
        }
    })

    $('#username').on('focusout keyup', function () {
        if ($("#username").val().length == 0) {
            document.getElementById('username').className = '';
            $('#username').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);
            $('#notifyUsername').addClass("text-danger-emphasis");
            $('#notifyUsername').html("This field can not be empty");
        } else {
            document.getElementById('username').className = '';
            document.getElementById('notifyUsername').className = '';
            $('#username').addClass("form-control");
            $('#notifyUsername').addClass("form-text");
            $('#notifyUsername').empty();
        }
    })

    $('#fname').on('focusout keyup', function () {
        if ($("#fname").val().length == 0) {
            document.getElementById('fname').className = '';
            $('#fname').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);
            $('#notifyFirst').addClass("text-danger-emphasis");
            $('#notifyFirst').html("This field can not be empty");
        } else {
            document.getElementById('fname').className = '';
            document.getElementById('notifyFirst').className = '';
            $('#fname').addClass("form-control");
            $('#notifyFirst').addClass("form-text");
            $('#notifyFirst').empty();
        }
    })

    $('#lname').on('focusout keyup', function () {
        if ($("#lname").val().length == 0) {
            document.getElementById('lname').className = '';
            $('#lname').addClass(["form-control", "bg-danger", "border border-3", "border-danger", "text-secondary", "bg-opacity-10"]);
            $('#notifyLastName').addClass("text-danger-emphasis");
            $('#notifyLastName').html("This field can not be empty");
        } else {
            document.getElementById('lname').className = '';
            document.getElementById('notifyLastName').className = '';
            $('#lname').addClass("form-control");
            $('#notifyLastName').addClass("form-text");
            $('#notifyLastName').empty();
        }
    })

})