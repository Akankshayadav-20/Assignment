let pendingChanges = [];

fetch("/user").then(res => res.json())
              .then(user => {
				document.getElementById("username").innerText=user.username;
			  }).catch(err => console.error(err));

function goBack(){
	window.location.href ="/dashboard";
}

function enableAll(){
	
	fetch("/enable-all").then(res => res.text())
	                    .then(msg => {
							alert(msg);
							location.reload();
						}).catch(err => console.error(err));
}

function disableAll(){
			
fetch("/disable-all").then(res => res.text())
			         .then(msg => {
					  alert(msg);
					location.reload();
			}).catch(err => console.error(err));
}

function deployChanges(){
	fetch("/deploy", {method:"POST",
		               headers:{
						"Content-Type":"application/json"
					   },
					   body:JSON.stringify(pendingChanges)
	})
	.then(res => res.text())
	.then(msg => {alert(msg);
     location.reload();}).catch(err => console.error(err));
}

function toggleRule(id,active){
	
	pendingChanges.push({
		id:id,active:active
	});
	
	alert("Changes added for deployment");
}

function viewRule(id){
		
		localStorage.setItem("ruleId",id);
		
		window.location.href="/rule-details";
	
}


fetch("http://localhost:8080/metadata")
.then(response => response.json())
.then(data => {
	
	let table = document.getElementById("rulesTable");
	 data.records.forEach(rule => {
		let status = rule.Active ? "Active" : "Inactive";
		
		let buttonText = rule.Active ? "Disable" : "Enable";
		
		table.innerHTML +=`<tr>
			               <td>${rule.ValidationName}</td>
						   <td>Account</td>
						   <td class="${rule.Active ? 'active' : 'inactive'}">${status}</td>
						   
						   <td>
							
							<button class="${rule.Active ? 'dbtn' : 'Ebtn'}" onclick="toggleRule('${rule.Id}',${rule.Active})">${buttonText}</button>
						   </td>
						   
						   <td>
						   <button class="viewBtn" onclick="viewRule('${rule.Id}')">view</button>
						   	</td>
						   
		</tr>
		`;		
		
	 });
}).catch(error => console.error(error));