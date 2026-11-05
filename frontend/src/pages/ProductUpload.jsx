import React, { useState } from 'react';
import { productService } from '../services/productService';
import { 
  Sparkles, 
  PlusCircle, 
  CheckCircle2, 
  AlertCircle, 
  Loader2, 
  Tag, 
  Layers, 
  ArrowRight,
  Bot
} from 'lucide-react';

export const ProductUpload = () => {
  // AI Generator state
  const [aiForm, setAiForm] = useState({
    productName: '',
    categoryHint: '',
    brand: '',
    color: '',
    material: '',
    gender: 'Unisex',
    provider: 'gemini',
  });
  const [aiLoading, setAiLoading] = useState(false);
  const [generatedCatalog, setGeneratedCatalog] = useState(null);
  const [aiError, setAiError] = useState('');

  // Manual / Editing state
  const [productForm, setProductForm] = useState({
    productName: '',
    description: '',
    category: '',
    price: '',
    brand: '',
    color: '',
    size: 'M',
    gender: 'Unisex',
    material: '',
    tags: '',
    source: 'MANUAL',
  });
  const [saveLoading, setSaveLoading] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState('');
  const [saveError, setSaveError] = useState('');

  const handleAiChange = (e) => {
    setAiForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleProductChange = (e) => {
    setProductForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleGenerateAi = async (e) => {
    e.preventDefault();
    if (!aiForm.productName.trim()) {
      setAiError('Please enter a product title or keyword');
      return;
    }

    setAiLoading(true);
    setAiError('');
    try {
      const result = await productService.generateAiCatalog(aiForm);
      setGeneratedCatalog(result);

      // Pre-fill manual product form with AI generated data
      setProductForm({
        productName: result.title || aiForm.productName,
        description: result.description || '',
        category: result.category || 'Apparel & Accessories',
        price: '1499.00',
        brand: result.brand || aiForm.brand || '',
        color: result.color || aiForm.color || '',
        size: result.size || 'M',
        gender: result.gender || aiForm.gender || 'Unisex',
        material: result.material || aiForm.material || '',
        tags: result.tags ? result.tags.join(', ') : '',
        source: 'AI',
      });
    } catch (err) {
      setAiError(err.response?.data?.message || 'Failed to generate catalog via AI');
    } finally {
      setAiLoading(false);
    }
  };

  const handleSaveProduct = async (e) => {
    e.preventDefault();
    if (!productForm.productName.trim()) {
      setSaveError('Product title is required');
      return;
    }

    setSaveLoading(true);
    setSaveError('');
    setSaveSuccess('');

    try {
      const tagList = productForm.tags
        ? productForm.tags.split(',').map((t) => t.trim()).filter((t) => t.length > 0)
        : [];

      await productService.createProduct({
        ...productForm,
        price: productForm.price ? parseFloat(productForm.price) : 0,
        tags: tagList,
      });

      setSaveSuccess('Product added to your ONDC catalog successfully!');
      // Reset form
      setProductForm({
        productName: '',
        description: '',
        category: '',
        price: '',
        brand: '',
        color: '',
        size: 'M',
        gender: 'Unisex',
        material: '',
        tags: '',
        source: 'MANUAL',
      });
      setGeneratedCatalog(null);
      setTimeout(() => setSaveSuccess(''), 4000);
    } catch (err) {
      setSaveError(err.response?.data?.message || 'Failed to save product');
    } finally {
      setSaveLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-5xl">
      <div>
        <h2 className="text-xl font-bold tracking-tight text-slate-900">
          AI Catalog Studio & Product Creator
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          Generate ONDC-compliant product titles, rich descriptions, taxonomy classifications, and tags using Google Gemini 2.5 Flash and Anthropic Claude 4.5.
        </p>
      </div>

      {saveSuccess && (
        <div className="flex items-center space-x-2 rounded-xl bg-emerald-50 p-4 text-xs font-semibold text-emerald-800 border border-emerald-200">
          <CheckCircle2 className="h-4 w-4 text-emerald-600 shrink-0" />
          <span>{saveSuccess}</span>
        </div>
      )}

      {/* AI Studio Section */}
      <div className="rounded-2xl border border-purple-200 bg-gradient-to-br from-purple-50/40 via-white to-indigo-50/30 p-6 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-2.5">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-purple-600 text-white shadow-md shadow-purple-500/20">
              <Sparkles className="h-5 w-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900">AI Fashion Catalog Assistant</h3>
              <p className="text-xs text-slate-500">Provide basic keyword or style, AI generates full ONDC schema</p>
            </div>
          </div>

          <div className="flex items-center space-x-2">
            <span className="text-xs font-semibold text-slate-500">Model:</span>
            <select
              name="provider"
              value={aiForm.provider}
              onChange={handleAiChange}
              className="rounded-lg border border-purple-200 bg-white px-2.5 py-1 text-xs font-medium text-slate-700 focus:outline-none focus:ring-2 focus:ring-purple-200"
            >
              <option value="gemini">Google Gemini 2.5 Flash</option>
              <option value="claude">Anthropic Claude Sonnet 4.5</option>
            </select>
          </div>
        </div>

        {aiError && (
          <div className="mb-4 flex items-center space-x-2 rounded-xl bg-red-50 p-3 text-xs text-red-700 border border-red-200">
            <AlertCircle className="h-4 w-4 shrink-0" />
            <span>{aiError}</span>
          </div>
        )}

        <form onSubmit={handleGenerateAi} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div className="sm:col-span-2">
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Product Name / Fashion Concept *
              </label>
              <input
                type="text"
                name="productName"
                required
                value={aiForm.productName}
                onChange={handleAiChange}
                placeholder="e.g. Slim-fit Men's Oxford Cotton Shirt, Casual White Sneakers, Floral Print Summer Dress"
                className="w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2.5 text-sm text-slate-900 focus:border-purple-600 focus:outline-none focus:ring-2 focus:ring-purple-100"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Gender Target
              </label>
              <select
                name="gender"
                value={aiForm.gender}
                onChange={handleAiChange}
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm focus:border-purple-600 focus:outline-none focus:ring-2 focus:ring-purple-100"
              >
                <option value="Men">Men</option>
                <option value="Women">Women</option>
                <option value="Unisex">Unisex</option>
                <option value="Boys">Boys</option>
                <option value="Girls">Girls</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Brand (Optional)</label>
              <input
                type="text"
                name="brand"
                value={aiForm.brand}
                onChange={handleAiChange}
                placeholder="e.g. Zara, Allen Solly, Urban Threads"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-purple-600 focus:outline-none focus:ring-2 focus:ring-purple-100"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Dominant Color</label>
              <input
                type="text"
                name="color"
                value={aiForm.color}
                onChange={handleAiChange}
                placeholder="e.g. Navy Blue, Olive Green"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-purple-600 focus:outline-none focus:ring-2 focus:ring-purple-100"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Fabric / Material</label>
              <input
                type="text"
                name="material"
                value={aiForm.material}
                onChange={handleAiChange}
                placeholder="e.g. 100% Combed Cotton, Linen"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-purple-600 focus:outline-none focus:ring-2 focus:ring-purple-100"
              />
            </div>
          </div>

          <div className="flex justify-end pt-1">
            <button
              type="submit"
              disabled={aiLoading}
              className="flex items-center space-x-2 rounded-xl bg-purple-600 px-5 py-2.5 text-xs font-bold text-white shadow-md shadow-purple-500/20 hover:bg-purple-700 transition disabled:opacity-50"
            >
              {aiLoading ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  <span>Synthesizing ONDC Catalog with {aiForm.provider === 'claude' ? 'Claude' : 'Gemini'}...</span>
                </>
              ) : (
                <>
                  <Sparkles className="h-4 w-4" />
                  <span>Generate ONDC Catalog Item</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>

      {/* Product Details Form */}
      <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex items-center justify-between mb-4 border-b border-slate-100 pb-3">
          <div>
            <h3 className="text-base font-bold text-slate-900">Review & Save Product</h3>
            <p className="text-xs text-slate-500">Fine-tune details before saving to your official catalog</p>
          </div>
          {productForm.source === 'AI' && (
            <span className="inline-flex items-center space-x-1 rounded-full bg-purple-50 px-2.5 py-0.5 text-xs font-bold text-purple-700 border border-purple-200">
              <Sparkles className="h-3 w-3" />
              <span>AI Enriched</span>
            </span>
          )}
        </div>

        {saveError && (
          <div className="mb-4 flex items-center space-x-2 rounded-xl bg-red-50 p-3 text-xs text-red-700 border border-red-200">
            <AlertCircle className="h-4 w-4 shrink-0" />
            <span>{saveError}</span>
          </div>
        )}

        <form onSubmit={handleSaveProduct} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Standardized Title *</label>
            <input
              type="text"
              name="productName"
              required
              value={productForm.productName}
              onChange={handleProductChange}
              placeholder="Full e-commerce product title"
              className="w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Detailed Description *</label>
            <textarea
              name="description"
              rows={3}
              required
              value={productForm.description}
              onChange={handleProductChange}
              placeholder="SEO description, fit, design, fabric weave, and occasion"
              className="w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">ONDC Taxonomy Category *</label>
              <input
                type="text"
                name="category"
                required
                value={productForm.category}
                onChange={handleProductChange}
                placeholder="Apparel & Accessories > Tops > Shirts"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Price (₹ INR) *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                name="price"
                required
                value={productForm.price}
                onChange={handleProductChange}
                placeholder="1299.00"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Size</label>
              <input
                type="text"
                name="size"
                value={productForm.size}
                onChange={handleProductChange}
                placeholder="M, L, XL, Free Size"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Brand</label>
              <input
                type="text"
                name="brand"
                value={productForm.brand}
                onChange={handleProductChange}
                placeholder="Brand name"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Color</label>
              <input
                type="text"
                name="color"
                value={productForm.color}
                onChange={handleProductChange}
                placeholder="Color"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Material</label>
              <input
                type="text"
                name="material"
                value={productForm.material}
                onChange={handleProductChange}
                placeholder="100% Cotton"
                className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Search Tags (Comma separated)</label>
            <input
              type="text"
              name="tags"
              value={productForm.tags}
              onChange={handleProductChange}
              placeholder="cotton, formal, casual, breathable, slimfit"
              className="w-full rounded-xl border border-slate-300 bg-white px-3.5 py-2 text-sm text-slate-900 focus:border-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div className="pt-2 flex justify-end">
            <button
              type="submit"
              disabled={saveLoading}
              className="flex items-center space-x-2 rounded-xl bg-blue-600 px-6 py-2.5 text-sm font-semibold text-white shadow-md shadow-blue-500/20 hover:bg-blue-700 transition disabled:opacity-50"
            >
              {saveLoading ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  <span>Saving Product...</span>
                </>
              ) : (
                <>
                  <PlusCircle className="h-4 w-4" />
                  <span>Add Product to ONDC Catalog</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProductUpload;

