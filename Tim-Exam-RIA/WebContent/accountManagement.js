(function() {
	
	  var personalMessage, accountList, accountDetails, createTransaction, confirmTransaction, indexentries,
	    balance, pageOrchestrator = new PageOrchestrator();
	  
	  window.addEventListener("load", () => {
		  makeCall("GET", "GetIndexBook", null, 
					function(req){
						if (req.readyState == XMLHttpRequest.DONE) {
							var response = req.responseText;
				            switch (req.status) {
				              case 200:
								indexentries = JSON.parse(req.responseText);
				                break;
				              case 400: // bad request
				                indexentries = null;
				                break;
				              case 500: // server error
				            	document.getElementById("errormessage").textContent = response;
				                break;
				            }
				          }
			});
		pageOrchestrator.start();
	    pageOrchestrator.refresh();
	  }, false);
	  
	  function AccountList(_alert, _accountscontainer, _accountscontainerbody) {
		    this.alert = _alert;
		    this.accountscontainer = _accountscontainer;
		    this.accountscontainerbody = _accountscontainerbody;

		    this.reset = function() {
		      this.accountscontainer.style.display = "none";
		      this.accountscontainerbody.innerHTML = "";
		    }

		    this.show = function() { 
		      var self = this;
		      makeCall("GET", "GetAccountList", null,
		        function(req) {
		          if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		              self.update(JSON.parse(req.responseText));
		            } else {
		              self.alert.style.display = "block"
		              self.alert.textContent = message;
		            }
		          }
		        }
		      );
		    };


		    this.update = function(arrayAccounts) {
		      var l = arrayAccounts.length,
		        elem, i, row, accountcell, linkcell, anchor;
		        this.accountscontainerbody.innerHTML = "";
		        var self = this;
		        arrayAccounts.forEach(function(account) {
		          row = document.createElement("tr");
		          accountcell = document.createElement("td");
		          accountcell.textContent = account;
		          row.appendChild(accountcell);
		          linkcell = document.createElement("td");
		          anchor = document.createElement("a");
		          linkcell.appendChild(anchor);
		          linkText = document.createTextNode("Show");
		          anchor.appendChild(linkText);
		          anchor.accountid = account;
		          anchor.setAttribute('accountid', account);
		          anchor.addEventListener("click", (e) => {
		        	sessionStorage.setItem('currentaccount', e.target.getAttribute("accountid"));
		            accountDetails.show(e.target.getAttribute("accountid"));
		          }, false);
		          anchor.href = "#";
		          row.appendChild(linkcell);
		          self.accountscontainerbody.appendChild(row);
		        });
		        this.accountscontainer.style.display = "block";
		      
		    }

		  }
	  
	  function AccountDetails(options) {
		    this.alert = options['alert'];
		    this.detailcontainer = options['detailcontainer'];
		    this.detailcontainerbody = options['detailcontainerbody'];
		    this.accountbalance = options['accountbalance'];
		    this.currentaccount = options['currentaccount'];
		    
		    this.show = function(accountid) {
		      this.currentaccount.textContent = "Current account: " + accountid;
		      self = this;
		      makeCall("GET", "GetAccountDetailsData?accountid=" + accountid, null,
		        function(req) {
		          if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		              var account = JSON.parse(req.responseText);
		              self.update(account);
		              self.detailcontainer.style.display = "block";
		              createTransaction.creator.style.display = "block";
		              createTransaction.button.style.display = "block";
		              personalMessage.infocontainer.style.display = "block";
					  createTransaction.initautocomplete();
		            } else if(req.status == 500){
		            	self.alert.style.display = "block";
		                self.alert.textContent = message;
		            } else {
		            	createTransaction.creator.style.display = "block";
		            	createTransaction.button.style.display = "block";
		            	personalMessage.infocontainer.style.display = "block";
		            }
		          }
		        }
		      );
		      
		      makeCall("GET", "GetAccountBalance?accountid=" + accountid, null, 
		    		  function(req) {
		          if (req.readyState == 4) {
			            var message = req.responseText;
			            if (req.status == 200) {
			              var balancetmp = JSON.parse(req.responseText);
			              balance = balancetmp;
			              self.accountbalance.textContent = "Account balance: " + balancetmp;
			            } else {
			              self.alert.textContent = message;
			            }
			          }
		    	  
		      });
		    };


		    this.reset = function() {
		      this.detailcontainer.style.display = "none";
		      this.detailcontainerbody.innerHTML = "";
		    }

		    this.update = function(arrayTransactions){
		    	this.detailcontainerbody.innerHTML = "";
		    	self = this;
		    	arrayTransactions.forEach(function(t) {
			        row = document.createElement("tr");
			        datecell = document.createElement("td");
			        datecell.textContent = t.date;
			        row.appendChild(datecell);
			        amountcell = document.createElement("td");
			        amountcell.textContent = t.amount;
			        row.appendChild(amountcell);
			        origincell = document.createElement("td");
			        origincell.textContent = t.originAccount;
			        row.appendChild(origincell);
			        destinationcell = document.createElement("td");
			        destinationcell.textContent = t.destinationAccount;
			        row.appendChild(destinationcell);
			        self.detailcontainerbody.appendChild(row);
		    	});
		    }
		  }
	  
	  function CreateTransaction(creator, button, alert, inp, inp2) {
		  
		  this.creator = creator;
		  this.alert = alert;
		  this.button = button;
		  this.inp = inp;
		  this.inp2 = inp2;
		  
		  document.addEventListener("click", function (e) {
			    closeAllLists(e.target);
			});
		  
		  this.initautocomplete = function(){
			  var arrtouser = [], arrtoaccount = [];
			  for (i=0; i<indexentries.length; i++){
				  arrtouser.push(indexentries[i].touser);
				  arrtoaccount.push(indexentries[i].toaccount);
			  }
			  arrtouser = arrtouser.filter((a, b) => arrtouser.indexOf(a) === b)
			  arrtoaccount = arrtoaccount.filter((a, b) => arrtoaccount.indexOf(a) === b)
			  this.autocomplete(this.inp, arrtouser);
			  this.autocomplete2(this.inp2, arrtoaccount);
		  }
		  
		  this.autocomplete2 = function(inp, arr){
			  inp.addEventListener("input", function(e) {				  
				  var a, b, i, val = this.value;
				  closeAllLists();
				  if (!val) {return false;}
				  a = document.createElement("DIV");
				  a.setAttribute("id", this.id + "autocomplete-list");
				  a.setAttribute("class", "autocomplete2-items");
				  this.parentNode.appendChild(a);	
				  
				  for (i=0; i<arr.length; i++){
					  if (arr[i].substr(0, val.length) == val){
						  b = document.createElement("DIV");
						  b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
						  b.innerHTML += arr[i].substr(val.length);
						  b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
						  b.addEventListener("click", function(e) {
				              inp.value = this.getElementsByTagName("input")[0].value;
				              closeAllLists();
				          });
						  a.appendChild(b);
					  }
				  }
			  });
			  
			  function closeAllLists(elmnt) {
				    var x = document.getElementsByClassName("autocomplete2-items");
				    for (var i = 0; i < x.length; i++) {
				      if (elmnt != x[i] && elmnt != inp) {
				      x[i].parentNode.removeChild(x[i]);
				    }
				  }
				}
		  };
		  
		  this.autocomplete = function(inp, arr){
			  inp.addEventListener("input", function(e) {
				  var a, b, i, val = this.value;
				  closeAllLists();
				  if (!val) {return false;}
				  a = document.createElement("DIV");
				  a.setAttribute("id", this.id + "autocomplete-list");
				  a.setAttribute("class", "autocomplete-items");
				  this.parentNode.appendChild(a);
				  
				  for (i=0; i<arr.length; i++){
					  if (arr[i].substr(0, val.length) == val){
						  b = document.createElement("DIV");
						  b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
						  b.innerHTML += arr[i].substr(val.length);
						  b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
						  b.addEventListener("click", function(e) {
				              inp.value = this.getElementsByTagName("input")[0].value;
				              closeAllLists();
				          });
						  a.appendChild(b);
					  }
				  }
			  });
			  
			  function closeAllLists(elmnt) {
				    var x = document.getElementsByClassName("autocomplete-items");
				    for (var i = 0; i < x.length; i++) {
				      if (elmnt != x[i] && elmnt != inp) {
				      x[i].parentNode.removeChild(x[i]);
				    }
				  }
				}
		  };
		  
		  this.reset = function() {
			  this.creator.style.display = "none";
			  this.button.style.display = "none";
			  button.addEventListener('click', (e) => {
				  
				  var form = e.target.closest("form");
				  var amounttmp = document.getElementById("id_formamount").value;
				  var destaccount = document.getElementById("id_inputaccount").value;
				  self = this;
				  
				  if (form.checkValidity()) {
					  if(document.getElementById("id_formamount").value > balance){
						  self.alert.style.display = "block";
		                  self.alert.textContent = "Not enough money";
					  }
					  
					  makeCall("GET", "GetAccountUser?account=" + destaccount, null,
						        function(req) {
						          if (req.readyState == 4) {
						        	  var response = req.responseText;
						            if (req.status == 200) {
						            	if(response.compareTo(document.getElementById("id_inputuser").value) == 0){
						            		
						            	}
						            	else{
						            		self.alert.style.display = "block";
							                self.alert.textContent = "Account doesn't belong to the user" + response + " " + document.getElementById("id_inputuser").value;
							                return;
						            	}
						            } else if (req.status == 500){
						            	  self.alert.style.display = "block";
						                  self.alert.textContent = response;
						                  return;
						            } else if (req.status == 400){
						            	self.alert.style.display = "block";
						            	self.alert.textContent = response;
						            	return;
						            }
						          }
						        }
						      );
					  
					  if(destaccount.length != 5) {
							self.alert.style.display = "block";
			                self.alert.textContent = "Invalid account 2";
							return;
						}
					  
					  if(isNaN(destaccount)){
						  self.alert.style.display = "block";
			              self.alert.textContent = "Account is not a number";
						  return;
					  }
					  
					  if(isNaN(amounttmp)){
						  self.alert.style.display = "block";
			                self.alert.textContent = "Amount has to be a number";
							return;
					  }					
					  
				  makeCall("POST", "CreateTransaction", e.target.closest("form"),
					        function(req) {
					          if (req.readyState == 4) {
					        	  var response = req.responseText;
					            if (req.status == 200) {
					            	message = JSON.parse(req.responseText);
					            	  confirmTransaction.show(message);
					            } else if (req.status == 500){
					            	  self.alert.style.display = "block";
					                  self.alert.textContent = response;
					            } else if (req.status == 400){
					            	self.alert.style.display = "block";
					            	self.alert.textContent = response;
					            }
					          }
					        }
					      );
				  }
				  else form.reportValidity();
			  }
		  );
		  }
	  }
	  
	  function ConfirmTransaction(options) {
		  this.alert = options['alert'];
		  this.confirmcontainer = options['confirmcontainer'];
		  this.originaccountuser = options['originaccountuser'];
		  this.originaccount = options['originaccount'];
		  this.originaccountamount = options['originaccountamount'];
		  this.destinationaccountuser = options['destinationaccountuser'];
		  this.destinationaccount = options['destinationaccount'];
		  this.destinationaccountamount = options['destinationaccountamount'];
		  this.gobackbutton = options['gobackbutton'];
		  this.addtoindex = options['addtoindex'];
		  this.addentrybutton = options['addentrybutton'];
		  this.isSaved;
		  
		  
		  this.reset = function() {
			  this.confirmcontainer.style.display = "none";
			  this.addtoindex.style.display = "none";
			  this.originaccountuser.textContent = "";
			  this.originaccount.textContent = "";
			  this.originaccountamount.textContent = "";
			  this.destinationaccountuser.textContent = "";
			  this.destinationaccount.textContent = "";
			  this.destinationaccountamount.textContent = "";
			  
			  this.gobackbutton.addEventListener('click', (e) => {
				  pageOrchestrator.refresh();
			  }
		  );	 
			  
			  this.addentrybutton.addEventListener('click', (e) => {
				  self = this;
				  if(e.target.closest("form").elements.namedItem("yes").checked
						  && !(self.destinationaccount.textContent === "")
						  && !(self.destinationaccountuser.textContent === "")){
					  makeCall("GET", "AddToIndexBook?destinationaccount=" + 
							  self.destinationaccount.textContent.substring(12) + "&destinationuser=" + 
							  self.destinationaccountuser.textContent.substring(9), null,
						        function(req) {
						  			if (req.readyState == XMLHttpRequest.DONE) 
						  				self.alert.textContent = "User info added";
						  			makeCall("GET", "GetIndexBook", null, 
											function(req){
												if (req.readyState == XMLHttpRequest.DONE) {
													var response = req.responseText;
										            switch (req.status) {
										              case 200:
														indexentries = JSON.parse(req.responseText);
										                break;
										              case 400: // bad request
										                indexentries = null;
										                break;
										              case 500: // server error
										            	document.getElementById("errormessage").textContent = response;
										                break;
										            }
										          }
									});
						        }
					  );
					}
				  pageOrchestrator.refresh();
			  }
		  );  
		  }
		  
		  this.show = function(info) {
			  accountList.reset();
			  accountDetails.reset();
			  createTransaction.reset();
			  personalMessage.reset();
			  this.confirmcontainer.style.display = "block";
			  this.originaccountuser.textContent = "From User: " + info.fromuser;
			  this.originaccount.textContent = "From Account: " + info.fromaccount;
			  this.originaccountamount.textContent = "Account Balance: " + info.frombalance
			  this.destinationaccountuser.textContent = "To User: " + info.touser;
			  this.destinationaccount.textContent = "To Account: " + info.toaccount;
			  this.destinationaccountamount.textContent = "Account Balance: " + info.tobalance;
		
			  isSaved = false;	
			  
			  if(indexentries != null){
				  indexentries.forEach(function(entry){
					  if(entry.touser.localeCompare(info.touser) == 0 && entry.toaccount.localeCompare(info.toaccount) == 0){
						  isSaved = true;
					  }
				  });
			  }
			  
			  if(!isSaved){
				  this.addtoindex.style.display = "block";
			  }
		  }
	  }
	  
	  function PersonalMessage(infocontainer, username, currentaccount, accountbalance, alert) {
		  this.infocontainer = infocontainer;
		  this.username = username;
		  this.currentaccount = currentaccount;
		  this.accountbalance = accountbalance;
		  this.alert = alert;
		  
		  this.reset = function() {
			  this.username.textContent = "Username: " + sessionStorage.getItem('username');
			  this.infocontainer.style.display = "none";
			  this.alert.style.display = "none";
			  this.currentaccount.textContent = "";
			  this.accountbalance.textContent = "";
		  }
	  }
	  
	  function PageOrchestrator() {
		    var alertContainer = document.getElementById("id_alert");
		    
		    this.start = function() {
		    	
		      accountList = new AccountList(
		        alertContainer,
		        document.getElementById("id_accountscontainer"),
		        document.getElementById("id_accountscontainerbody"));
		      
		      accountDetails = new AccountDetails({ // many parameters, wrap them in an
		        // object
		        alert: alertContainer,
		        detailcontainer: document.getElementById("id_transactionscontainer"),
		        detailcontainerbody: document.getElementById("id_transactionscontainerbody"),
		        accountbalance: document.getElementById("id_accountbalance"),
		        currentaccount: document.getElementById("id_currentaccount")
		      });
		      
		      createTransaction = new CreateTransaction(document.getElementById("id_createtransactionform"), document.getElementById("id_submitbutton"), alertContainer,
		    		  document.getElementById("id_inputuser"), document.getElementById("id_inputaccount"));
		      
		      personalMessage = new PersonalMessage(document.getElementById("id_generalinfo"), document.getElementById("id_username"),
		    		  document.getElementById("id_currentaccount"), document.getElementById("id_accountbalance"), alertContainer);
		      
		      confirmTransaction = new ConfirmTransaction({
		    	  alert: alertContainer,
			      confirmcontainer: document.getElementById("id_confirmcontainer"),
			      originaccountuser: document.getElementById("id_originaccountuser"),
			      originaccount: document.getElementById("id_originaccount"),
			      originaccountamount: document.getElementById("id_originaccountamount"),
			      destinationaccountuser: document.getElementById("id_destinationaccountuser"),
			      destinationaccount: document.getElementById("id_destinationaccount"),
			      destinationaccountamount: document.getElementById("id_destinationaccountamount"),
			      gobackbutton: document.getElementById("id_backbutton"),
			      addtoindex: document.getElementById("id_addtoindex"),
			      addentrybutton: document.getElementById("id_addtobutton"),
		      });
		    };


		    this.refresh = function() {
		      accountList.reset();
		      accountDetails.reset();
		      createTransaction.reset();
		      confirmTransaction.reset();
		      personalMessage.reset();
		      accountList.show(); 
		    };
		  }
})();