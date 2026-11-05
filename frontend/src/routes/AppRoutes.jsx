import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from '../components/ProtectedRoute';
import App from '../App';

import Login from '../pages/Login';
import Register from '../pages/Register';
import Dashboard from '../pages/Dashboard';
import SellerProfile from '../pages/SellerProfile';
import UploadDocuments from '../pages/UploadDocuments';
import ProductUpload from '../pages/ProductUpload';
import Catalog from '../pages/Catalog';
import Compliance from '../pages/Compliance';

export const AppRoutes = () => {
  return (
    <Routes>
      {/* Public routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Authenticated protected routes */}
      <Route element={<ProtectedRoute />}>
        <Route element={<App />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/profile" element={<SellerProfile />} />
          <Route path="/documents" element={<UploadDocuments />} />
          <Route path="/product-upload" element={<ProductUpload />} />
          <Route path="/catalog" element={<Catalog />} />
          <Route path="/compliance" element={<Compliance />} />
        </Route>
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};

export default AppRoutes;
