import React from 'react';
import { Tag, Sparkles, Trash2, Layers } from 'lucide-react';

export const ProductCard = ({ product, onDelete }) => {
  return (
    <div className="flex flex-col justify-between rounded-2xl border border-slate-200 bg-white p-5 shadow-xs hover:shadow-md transition">
      <div>
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-2">
            <span className={`inline-flex items-center space-x-1 rounded-full px-2.5 py-0.5 text-[11px] font-bold ${
              product.source === 'AI'
                ? 'bg-purple-50 text-purple-700 border border-purple-200'
                : 'bg-slate-100 text-slate-700'
            }`}>
              {product.source === 'AI' && <Sparkles className="h-3 w-3 mr-0.5" />}
              <span>{product.source === 'AI' ? 'AI Generated' : 'Manual'}</span>
            </span>
            <span className="text-xs text-slate-400 font-mono">
              {product.gender || 'Unisex'}
            </span>
          </div>

          {onDelete && (
            <button
              onClick={() => onDelete(product.id)}
              className="text-slate-400 hover:text-red-600 transition p-1"
              title="Delete item"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          )}
        </div>

        <h4 className="mt-3 text-base font-bold text-slate-900 leading-snug line-clamp-1">
          {product.productName}
        </h4>
        <p className="text-xs text-blue-600 font-medium mt-0.5 line-clamp-1">
          {product.category || 'Fashion & Apparel'}
        </p>

        <p className="mt-2 text-xs text-slate-600 line-clamp-2 leading-relaxed">
          {product.description || 'No description provided.'}
        </p>

        <div className="mt-4 grid grid-cols-2 gap-2 text-[11px] bg-slate-50 p-2.5 rounded-xl border border-slate-100">
          <div>
            <span className="text-slate-400">Brand: </span>
            <span className="font-semibold text-slate-700">{product.brand || 'Generic'}</span>
          </div>
          <div>
            <span className="text-slate-400">Color: </span>
            <span className="font-semibold text-slate-700">{product.color || 'Standard'}</span>
          </div>
          <div>
            <span className="text-slate-400">Size: </span>
            <span className="font-semibold text-slate-700">{product.size || 'M'}</span>
          </div>
          <div>
            <span className="text-slate-400">Material: </span>
            <span className="font-semibold text-slate-700 truncate block">{product.material || 'Cotton'}</span>
          </div>
        </div>

        {product.tags && product.tags.length > 0 && (
          <div className="mt-3 flex flex-wrap gap-1">
            {product.tags.slice(0, 4).map((tag, idx) => (
              <span key={idx} className="inline-flex items-center space-x-1 rounded-md bg-slate-100 px-2 py-0.5 text-[10px] font-medium text-slate-600">
                <Tag className="h-2.5 w-2.5 text-slate-400" />
                <span>{tag}</span>
              </span>
            ))}
          </div>
        )}
      </div>

      <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between">
        <div>
          <span className="text-[10px] uppercase font-bold text-slate-400">ONDC Price</span>
          <p className="text-base font-extrabold text-slate-900">
            ₹{product.price ? Number(product.price).toLocaleString('en-IN') : '0'}
          </p>
        </div>
        <div className="rounded-lg bg-blue-50 px-2 py-1 text-[11px] font-semibold text-blue-700">
          Ready for Sync
        </div>
      </div>
    </div>
  );
};

export default ProductCard;

