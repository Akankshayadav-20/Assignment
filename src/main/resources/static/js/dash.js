//to get meta data

fetch(window.location.origin + "/user")
	.then(res => {

		if (!res.ok) {
			window.location.href = "/login";
			return;

		}

		return res.json();
	}).then(user => {
		if (!user) return;

		document.getElementById("username").innerText = user.username;

		document.getElementById("organization").innerText = user.organization_id;

	}).catch(err => {
		console.error(err);
		window.location.href = "/login";
	});

function getMetadata() {
	window.location.href = "/validation";
}

function logout() {
	fetch("/logout").then(res => res.text())
		.then((msg) => {
			alert(msg)
			window.location.href = "/login";

		}).catch(err => console.error(err));

}