import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080'
});

export async function getRatingsByCakeId(cakeId) {
  const response = await api.get(`/api/ratings/ratings/cake/${cakeId}`);
  return response.data;
}

export async function getAverageRating(cakeId) {
  const response = await api.get(`/api/ratings/ratings/cake/${cakeId}/average`);
  return response.data;
}

export async function createRating(ratingData) {
  const response = await api.post('/api/ratings/ratings', ratingData);
  return response.data;
}

export async function getCakes() {
  const response = await api.get('/api/catalog/cakes');
  return response.data;
}

export async function getBasket() {
  const response = await api.get('/api/orders/basket');
  return response.data;
}

export async function updateBasketItem(id, payload) {
  const response = await api.put(`/api/orders/basket/${id}`, payload);
  return response.data;
}

export async function removeBasketItem(id) {
  await api.delete(`/api/orders/basket/${id}`);
}

export async function checkout() {
  const response = await api.post('/api/orders/checkout');
  return response.data;
}

export async function getNotifications() {
  const response = await api.get('/api/notifications/notifications');
  return response.data;
}

export default api;
