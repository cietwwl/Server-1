var Login = function () {
    
    return {
        //main function to initiate the module
        init: function () {
        	
           $('.login-form').validate({
	            errorElement: 'label', //default input error message container
	            errorClass: 'help-inline', // default input error message class
	            focusInvalid: false, // do not focus the last invalid input
	            rules: {
	                username: {
	                    required: true
	                },
	                password: {
	                    required: true
	                },
	                remember: {
	                    required: false
	                }
	            },

	            messages: {
	                username: {
	                    required: "Username is required."
	                },
	                password: {
	                    required: "Password is required."
	                }
	            },

	            invalidHandler: function (event, validator) { //display error alert on form submit   
	                $('.alert-error', $('.login-form')).show();
	            },

	            highlight: function (element) { // hightlight error inputs
	                $(element).closest('.control-group').addClass('error'); // set error class to the control group
	            },

	            success: function (label) {
	                label.closest('.control-group').removeClass('error');
	                label.remove();
	            },

	            errorPlacement: function (error, element) {
	                error.addClass('help-small no-left-padding').insertAfter(element.closest('.input-icon'));
	            },

	            submitHandler: function (form) {
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
								$.cookie(Cookie.TOKEN,response.data.token);
								$.cookie(Cookie.USERNAME,response.data.account);
								location.href='view/main.html';
							}else{
								alert(response.meta.msg);
							}
						}
					})
	            }
	        });

	        $('.login-form input').keypress(function (e) {
	            if (e.which == 13) {
	                if ($('.login-form').validate().form()) {
	                    window.location.href = "index.html";
	                }
	                return false;
	            }
	        });

	        $('.forget-form').validate({
	            errorElement: 'label', //default input error message container
	            errorClass: 'help-inline', // default input error message class
	            focusInvalid: false, // do not focus the last invalid input
	            ignore: "",
	            rules: {
	                email: {
	                    required: true,
	                    email: true
	                }
	            },

	            messages: {
	                email: {
	                    required: "Email is required."
	                }
	            },

	            invalidHandler: function (event, validator) { //display error alert on form submit   
					$('.alert-danger', $('.login-form')).show();
	            },

	            highlight: function (element) { // hightlight error inputs
	                $(element).closest('.control-group').addClass('error'); // set error class to the control group
	            },

	            success: function (label) {
	                label.closest('.control-group').removeClass('error');
	                label.remove();
	            },

	            errorPlacement: function (error, element) {
	                error.addClass('help-small no-left-padding').insertAfter(element.closest('.input-icon'));
	            },

	            submitHandler: function (form) {
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

	        $('.forget-form input').keypress(function (e) {
	            if (e.which == 13) {
	                if ($('.forget-form').validate().form()) {
	                    window.location.href = "index.html";
	                }
	                return false;
	            }
	        });

	        jQuery('#forget-password').click(function () {
	            jQuery('.login-form').hide();
	            jQuery('.forget-form').show();
	        });

	        jQuery('#back-btn').click(function () {
	            jQuery('.login-form').show();
	            jQuery('.forget-form').hide();
	        });

	        $('.register-form').validate({
	            errorElement: 'label', //default input error message container
	            errorClass: 'help-inline', // default input error message class
	            focusInvalid: false, // do not focus the last invalid input
	            ignore: "",
	            rules: {
	                username: {
	                    required: true
	                },
	                password: {
	                    required: true
	                },
	                rpassword: {
	                    equalTo: "#register_password"
	                },
	                email: {
	                    required: true,
	                    email: true
	                },
	                tnc: {
	                    required: true
	                }
	            },

	            messages: { // custom messages for radio buttons and checkboxes
	                tnc: {
	                    required: "Please accept TNC first."
	                }
	            },

	            invalidHandler: function (event, validator) { //display error alert on form submit   
					$('.alert-error', $('.login-form')).show();
	            },

	            highlight: function (element) { // hightlight error inputs
	                $(element).closest('.control-group').addClass('error'); // set error class to the control group
	            },

	            success: function (label) {
	                label.closest('.control-group').removeClass('error');
	                label.remove();
	            },

	            errorPlacement: function (error, element) {
	                if (element.attr("name") == "tnc") { // insert checkbox errors after the container                  
	                    error.addClass('help-small no-left-padding').insertAfter($('#register_tnc_error'));
	                } else {
	                    error.addClass('help-small no-left-padding').insertAfter(element.closest('.input-icon'));
	                }
	            },

	            submitHandler: function (form) {
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
							if(response.meta.success){
								//登录成功,保存cookie
								$.cookie(Cookie.USERNAME,response.data.account)
								location.href='view/main.html';
							}else{
								alert(response.meta.msg);
							}
						}
					})
	            }
	        });

	        jQuery('#register-btn').click(function () {
	            jQuery('.login-form').hide();
	            jQuery('.register-form').show();
	        });

	        jQuery('#register-back-btn').click(function () {
	            jQuery('.login-form').show();
	            jQuery('.register-form').hide();
	        });

	        $.backstretch([
		        "media/image/bg/1.jpg",
		        "media/image/bg/2.jpg",
		        "media/image/bg/3.jpg",
		        "media/image/bg/4.jpg"
		        ], {
		          fade: 1000,
		          duration: 8000
		      });
        }

    };

}();