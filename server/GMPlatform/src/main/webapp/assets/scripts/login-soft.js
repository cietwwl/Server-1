var Login = function() {

    var handleLogin = function() {
        $('.login-form').validate({
            errorElement : 'span', // default input error message container
            errorClass : 'help-block', // default input error message class
            focusInvalid : false, // do not focus the last invalid input
            rules : {
                username : {
                    required : true
                },
                password : {
                    required : true
                },
                remember : {
                    required : false
                }
            },

            messages : {
            	username : {
                    required : "用户名不能为空."
                },
                password : {
                    required : "密码不能为空."
                }
            },

            invalidHandler : function(event, validator) { // display error
                // alert on form
                // submit
                $('.alert-danger', $('.login-form')).show();
            },

            highlight : function(element) { // hightlight error inputs
                $(element).closest('.form-group').addClass('has-error'); // set
                // error
                // class
                // to
                // the
                // control
                // group
            },

            success : function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement : function(error, element) {
                error.insertAfter(element.closest('.input-icon'));
            },

            submitHandler : function(form) {
            	var account = $('#username').val();
            	var passwordInput = $('#password').val();
            	var pwd = sha256_digest(passwordInput);
            	var remember = $('#remember').val();
            	var data = {"account":account,"password":pwd, "remember":remember};
                //在此执行提交
                $.ajax({
                	type:'post',
                	url:'user/login',
                	contentType:'application/json',
                	dataType:'json',//可能返回的参数
                	data:JSON.stringify(data),
                	success:function(response){
                		if(response.meta.success){
                			//登录成功,保存cookie
                			//$.cookie(Cookie.TOKEN,response.data.token);
                			//$.cookie(Cookie.USERNAME,response.data.account);
                			location.href='view/main.html';
                		}else{
                			alert(response.meta.msg);
                		}
                	}
                })
                
            }
            
            
        });

        $('.login-form input').keypress(function(e) {
            if (e.which == 13) {
                if ($('.login-form').validate().form()) {
                    $('.login-form').submit();
                    swal("submit form 4444444~")
                }
                return false;
            }
        });
        

    }

    var handleForgetPassword = function() {
        $('.forget-form').validate({
            errorElement : 'span', // default input error message container
            errorClass : 'help-block', // default input error message class
            focusInvalid : false, // do not focus the last invalid input
            ignore : "",
            rules : {
                email : {
                    required : true,
                    email : true
                }
            },

            messages : {
                email : {
                    required : "Email is required."
                }
            },

            invalidHandler : function(event, validator) { // display error
                // alert on form
                // submit

            },

            highlight : function(element) { // hightlight error inputs
                $(element).closest('.form-group').addClass('has-error'); // set
                // error
                // class
                // to
                // the
                // control
                // group
            },

            success : function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement : function(error, element) {
                error.insertAfter(element.closest('.input-icon'));
            },

            submitHandler : function(form) {
                form.submit();
                swal("submit form 111111111111");
            }
        });

        $('.forget-form input').keypress(function(e) {
            if (e.which == 13) {
                if ($('.forget-form').validate().form()) {
                    $('.forget-form').submit();
                    swal("submit form 22222222222222");
                }
                return false;
            }
        });

        jQuery('#forget-password').click(function() {
            jQuery('.login-form').hide();
            jQuery('.forget-form').show();
        });

        jQuery('#back-btn').click(function() {
            jQuery('.login-form').show();
            jQuery('.forget-form').hide();
        });

    }

    var handleRegister = function() {

        function format(state) {
            if (!state.id)
                return state.text; // optgroup
            return "<img class='flag' src='assets/img/flags/"
                    + state.id.toLowerCase() + ".png'/>&nbsp;&nbsp;"
                    + state.text;
        }

        $("#select2_sample4")
                .select2(
                        {
                            placeholder : '<i class="fa fa-map-marker"></i>&nbsp;Select a Country',
                            allowClear : true,
                            formatResult : format,
                            formatSelection : format,
                            escapeMarkup : function(m) {
                                return m;
                            }
                        });

        $('#select2_sample4').change(function() {
            $('.register-form').validate().element($(this)); // revalidate
            // the chosen
            // dropdown
            // value and
            // show error or
            // success
            // message for
            // the input
        });

        $('.register-form').validate({
            errorElement : 'span', // default input error message container
            errorClass : 'help-block', // default input error message class
            focusInvalid : false, // do not focus the last invalid input
            ignore : "",
            rules : {

//                fullname : {
//                    required : true
//                },
//                email : {
//                    required : true,
//                    email : true
//                },
//                address : {
//                    required : true
//                },
//                city : {
//                    required : true
//                },
//                country : {
//                    required : true
//                },

                username : {
                    required : true
                },
                password : {
                    required : true
                },
                rpassword : {
                    equalTo : "#registerPwd"
                },

//                tnc : {
//                    required : true
//                }
            },

            messages : { // custom messages for radio buttons and checkboxes
//                tnc : {
//                    required : "Please accept TNC first."
//                }
            },

            invalidHandler : function(event, validator) { // display error
                // alert on form
                // submit

            },

            highlight : function(element) { // hightlight error inputs
                $(element).closest('.form-group').addClass('has-error'); // set
                // error
                // class
                // to
                // the
                // control
                // group
            },

            success : function(label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement : function(error, element) {
                if (element.attr("name") == "tnc") { // insert checkbox
                    // errors after the
                    // container
                    error.insertAfter($('#register_tnc_error'));
                } else if (element.closest('.input-icon').size() === 1) {
                    error.insertAfter(element.closest('.input-icon'));
                } else {
                    error.insertAfter(element);
                }
            },

            submitHandler : function(form) {
            	var account = $('#account').val();
            	var passwordInput = $('#registerPwd').val();
            	var pwd = sha256_digest(passwordInput);
            	var data = {"account":account,"password":pwd};
                //在此执行提交
                $.ajax({
                	type:'post',
                	url:'user/register',
                	contentType:'application/json',
                	dataType:'json',//可能返回的参数
                	data:JSON.stringify(data),
                	success:function(response){
                		//var resData = JSON.parse(response)
                		if(response.meta.success){
                			//登录成功,保存cookie
                			//$.cookie(Cookie.TOKEN,response.data.token);
                			$.cookie(Cookie.USERNAME,response.data.account)
                			location.href='view/main.html';
                		}else{
                			alert(response.meta.msg);
                		}
                	}
                })
                
            }
        });

        $('.register-form input').keypress(function(e) {
            if (e.which == 13) {
                if ($('.register-form').validate().form()) {
                    $('.register-form').submit();
                }
                return false;
            }
        });

        jQuery('#register-btn').click(function() {
            jQuery('.login-form').hide();
            jQuery('.register-form').show();
        });

        jQuery('#register-back-btn').click(function() {
            jQuery('.login-form').show();
            jQuery('.register-form').hide();
        });
    }

    return {
        // main function to initiate the module
        init : function() {
            console.log(12);
            handleLogin();
            handleForgetPassword();
            handleRegister();

            $.backstretch([ "assets/img/bg/1.jpg", "assets/img/bg/2.jpg",
                    "assets/img/bg/3.jpg", "assets/img/bg/4.jpg" ], {
                fade : 1000,
                duration : 8000
            });
        }

    };

}();