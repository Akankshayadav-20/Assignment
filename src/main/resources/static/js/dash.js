//to get meta data

fetch("/user").then(res => res.json()).then(user => {
	
	document.getElementById("username").innerText = user.username;
	
	document.getElementById("organization").innerText = user.organization_id;

  });


function getMetadata(){
	window.location.href= "/validation";
}

function logout(){
	fetch("/logout").then(res => res.text())
	                .then((msg)=>{
						alert(msg)
						window.location.href="/login";
					
	}).catch(err => console.error(err));
						
}