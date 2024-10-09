const stripe = Stripe('pk_test_51Q54GDP4ZZidmJveRIahXGho4cefpxGSErrP6RWjpxAj3fpMH3gGQqulCuF9sXprcWK7jEjx2yvSPJdkiBUUL3XC00vkwY3o7T');
const paymentButton = document.querySelector('#paymentButton');
const cardButton = document.querySelector('#cardButton')
//const form = document.getElementById('payment-form');

paymentButton.addEventListener('click', () => {
	stripe.redirectToCheckout({
		sessionId: sessionId
	})
});

