import { useEffect, useState } from 'react';
import { getBasket, updateBasketItem, removeBasketItem, checkout } from '../services/api';

function BasketPage() {
  const [basket, setBasket] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState('');

  const loadBasket = async () => {
    setLoading(true);
    setError(null);
    setMessage('');

    try {
      const items = await getBasket();
      setBasket(items);
    } catch (err) {
      setError('Failed to load basket.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBasket();
  }, []);

  const changeQuantity = async (item, newQuantity) => {
    if (newQuantity < 1) {
      return;
    }

    try {
      await updateBasketItem(item.id, {
        cakeId: item.cakeId,
        cakeName: item.cakeName,
        price: item.price,
        quantity: newQuantity
      });
      await loadBasket();
    } catch (err) {
      setError('Unable to update quantity.');
    }
  };

  const handleRemove = async (id) => {
    try {
      await removeBasketItem(id);
      await loadBasket();
    } catch (err) {
      setError('Unable to remove item.');
    }
  };

  const handleCheckout = async () => {
    setError(null);
    setMessage('');

    try {
      await checkout();
      setMessage('Checkout successful.');
      await loadBasket();
    } catch (err) {
      setError('Checkout failed.');
    }
  };

  return (
    <div>
      <h1>Basket</h1>

      {loading && <p>Loading...</p>}
      {error && <p className="status-message status-error">{error}</p>}
      {message && <p className="status-message status-success">{message}</p>}

      {!loading && !error && (
        <>
          {basket.length === 0 ? (
            <p>Your basket is empty.</p>
          ) : (
            <table className="basket-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Cake ID</th>
                  <th>Name</th>
                  <th>Quantity</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {basket.map((item) => (
                  <tr key={item.id}>
                    <td>{item.id}</td>
                    <td>{item.cakeId}</td>
                    <td>{item.cakeName}</td>
                    <td>{item.quantity}</td>
                    <td className="basket-actions">
                      <button
                        type="button"
                        onClick={() => changeQuantity(item, item.quantity - 1)}
                        disabled={item.quantity <= 1}
                      >
                        -
                      </button>
                      <button
                        type="button"
                        onClick={() => changeQuantity(item, item.quantity + 1)}
                      >
                        +
                      </button>
                      <button type="button" className="remove-button" onClick={() => handleRemove(item.id)}>
                        Remove
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          <div className="checkout-panel">
            <button type="button" onClick={handleCheckout} disabled={basket.length === 0}>
              Checkout
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default BasketPage;
