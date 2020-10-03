(function() { 
	
    document.getElementById("id_loginform").style.display = "block";
    document.getElementById("id_registrationform").style.display = "none";
	
	  document.getElementById("registrationbutton").addEventListener('click', (e) => {
		  var form = e.target.closest("form");
		    if (form.checkValidity() && 
		    	document.getElementById("pwd").value === document.getElementById("pwdconf").value) {
		    	if(document.getElementById("username").value.replace(" ","").isEmpty || document.getElementById("pwd").value.replace(" ","").isEmpty || document.getElementById("pwdconf").value.replace(" ","").isEmpty){
		    		document.getElementById("confirmerrormessage").textContent = "No blank field allowed";
		    		return;
		    	}
		    	else if(document.getElementById("username").value.length > 20 || document.getElementById("pwd").value.length > 20){
		    		document.getElementById("confirmerrormessage").textContent = "Max 20 characters allowed";
		    		return;
		    	}
		      makeCall("POST", 'Register', e.target.closest("form"),
		        function(req) {  
		          if (req.readyState == XMLHttpRequest.DONE) {
		        	  var response = req.responseText;
		            switch (req.status) {
		              case 200:
			      		document.getElementById("id_loginform").style.display = "block";
			    		document.getElementById("id_registrationform").style.display = "none";
			    		document.getElementById("errormessage").textContent = response;
		                break;
		              case 400: // bad request
		                document.getElementById("confirmerrormessage").textContent = response;
		                break;
		              case 401: // unauthorized
		                  document.getElementById("confirmerrormessage").textContent = response;
		                  break;
		              case 500: // server error
		            	document.getElementById("confirmerrormessage").textContent = response;
		                break;
		            }
		          }
		        }
		      );
		    } else {
		    	 document.getElementById("confirmerrormessage").textContent = "Different confirm password";
		    }
	  });

	  document.getElementById("selectsignin").addEventListener('click', (e) => {
		    document.getElementById("id_loginform").style.display = "none";
		    document.getElementById("id_registrationform").style.display = "block";

	  });
	  
	  document.getElementById("selectlogin").addEventListener('click', (e) => {
		    document.getElementById("id_loginform").style.display = "block";
		    document.getElementById("id_registrationform").style.display = "none";

	  });
	
  document.getElementById("loginbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'CheckLogin', e.target.closest("form"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
            	sessionStorage.setItem('username', message);
                window.location.href = "Home.html";
                break;
              case 400: // bad request
                document.getElementById("errormessage").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("errormessage").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("errormessage").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();