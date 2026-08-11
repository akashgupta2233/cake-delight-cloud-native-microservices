import { useEffect, useState } from 'react';
import { getNotifications } from '../services/api';

function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const formatDate = (dateString) => {
    if (!dateString) {
      return '-';
    }

    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) {
      return dateString;
    }

    return date.toLocaleString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const loadNotifications = async () => {
    setLoading(true);
    setError('');

    try {
      const data = await getNotifications();
      setNotifications(data);
    } catch (err) {
      setError('Failed to load notifications.');
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
    const interval = setInterval(loadNotifications, 5000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div>
      <h1>Notifications</h1>

      {loading && <p>Loading...</p>}
      {error && <p className="status-message status-error">{error}</p>}

      {!loading && !error && notifications.length === 0 && (
        <p>No notifications yet.</p>
      )}

      {!loading && !error && notifications.length > 0 && (
        <table className="notification-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Order ID</th>
              <th>Message</th>
              <th>Status</th>
              <th>Created At</th>
            </tr>
          </thead>
          <tbody>
            {notifications.map((notification) => (
              <tr key={notification.id}>
                <td>{notification.id}</td>
                <td>{notification.orderId}</td>
                <td>{notification.message}</td>
                <td>{notification.status}</td>
                <td>{formatDate(notification.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default NotificationsPage;
