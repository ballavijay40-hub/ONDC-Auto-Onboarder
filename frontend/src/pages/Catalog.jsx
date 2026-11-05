import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { productService } from '../services/productService';
import { dashboardService } from '../services/dashboardService';
import ProductCard from '../components/ProductCard';
import { 
  ShoppingBag, 
  PlusCircle, 
  Download, 
  Search, 
  Filter, 
  Loader2, 
  AlertCircle,
  FileJson
} from 'lucide-react';

export const Catalog = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFilter, setSelectedFilter] = useState('ALL');
  const [exportLoading, setExportLoading] = useState(false);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const list = await productService.getMyProducts();
      setProducts(list);
      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load catalog products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    try {
      await productService.deleteProduct(id);
      setProducts((prev) => prev.filter((p) => p.id !== id));
    } catch (err) {
      alert('Failed to delete product: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleExportJson = async () => {
    try {
      setExportLoading(true);
      const catalog = await dashboardService.exportBecknCatalog();
      const blob = new Blob([JSON.stringify(catalog, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `ondc-retail-catalog-${Date.now()}.json`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      alert('Failed to export catalog: ' + (err.response?.data?.message || err.message));
    } finally {
      setExportLoading(false);
    }
  };

  const filteredProducts = products.filter((p) => {
    const matchesSearch =
      (p.productName && p.productName.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (p.category && p.category.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (p.brand && p.brand.toLowerCase().includes(searchTerm.toLowerCase()));

    if (selectedFilter === 'AI') return matchesSearch && p.source === 'AI';
    if (selectedFilter === 'MANUAL') return matchesSearch && p.source === 'MANUAL';
    return matchesSearch;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-slate-900">
            Product Catalog & ONDC Beckn Export
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Browse, manage, and export your fashion & apparel catalog formatted for ONDC retail network discovery.
          </p>
        </div>

        <div className="flex items-center space-x-2.5">
          <button
            onClick={handleExportJson}
            disabled={exportLoading || products.length === 0}
            className="flex items-center space-x-1.5 rounded-xl border border-slate-300 bg-white hover:bg-slate-50 px-4 py-2.5 text-xs font-bold text-slate-700 shadow-xs transition disabled:opacity-50"
          >
            {exportLoading ? (
              <Loader2 className="h-4 w-4 animate-spin text-blue-600" />
            ) : (
              <Download className="h-4 w-4 text-blue-600" />
            )}
            <span>Export Beckn JSON</span>
          </button>

          <Link
            to="/product-upload"
            className="flex items-center space-x-1.5 rounded-xl bg-blue-600 hover:bg-blue-700 px-4 py-2.5 text-xs font-bold text-white shadow-md shadow-blue-500/20 transition"
          >
            <PlusCircle className="h-4 w-4" />
            <span>Add New Item</span>
          </Link>
        </div>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl bg-red-50 p-4 text-xs text-red-700 border border-red-200">
          <AlertCircle className="h-4 w-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-white p-3.5 shadow-2xs">
        <div className="relative w-full sm:w-80">
          <Search className="absolute left-3 top-2.5 h-4 w-4 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by title, brand, or category..."
            className="w-full rounded-xl border border-slate-200 bg-slate-50 pl-9 pr-3 py-1.5 text-xs text-slate-900 focus:bg-white focus:border-blue-500 focus:outline-none"
          />
        </div>

        <div className="flex items-center space-x-1.5 w-full sm:w-auto">
          <span className="text-xs text-slate-400 font-medium mr-1 flex items-center">
            <Filter className="h-3 w-3 mr-1" /> Source:
          </span>
          {['ALL', 'AI', 'MANUAL'].map((filter) => (
            <button
              key={filter}
              onClick={() => setSelectedFilter(filter)}
              className={`rounded-lg px-2.5 py-1 text-xs font-bold transition ${
                selectedFilter === filter
                  ? 'bg-blue-600 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {filter}
            </button>
          ))}
        </div>
      </div>

      {/* Product Grid */}
      {loading ? (
        <div className="flex h-64 items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
        </div>
      ) : filteredProducts.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-16 text-center rounded-2xl border-2 border-dashed border-slate-200 bg-white p-8">
          <ShoppingBag className="h-12 w-12 text-slate-300 mb-3" />
          <h3 className="text-base font-bold text-slate-800">No products found</h3>
          <p className="text-xs text-slate-400 mt-1 max-w-sm mb-4">
            {searchTerm
              ? 'No products matched your search keyword.'
              : 'Add your first apparel item using the AI Catalog Studio to make your store discoverable.'}
          </p>
          <Link
            to="/product-upload"
            className="inline-flex items-center space-x-1.5 rounded-xl bg-blue-600 px-4 py-2 text-xs font-semibold text-white shadow-sm hover:bg-blue-700"
          >
            <PlusCircle className="h-4 w-4" />
            <span>Launch AI Studio</span>
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredProducts.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default Catalog;
