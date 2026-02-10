
/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
*/


import React, { useState } from 'react';
import { Product } from '../types';
import { orderApi, OrderRequest } from '../services/api';

interface CheckoutProps {
  items: Product[];
  onBack: () => void;
  onOrderComplete?: () => void;  // Callback to clear cart after order
}

const Checkout: React.FC<CheckoutProps> = ({ items, onBack, onOrderComplete }) => {
  const [formData, setFormData] = useState({
    email: '',
    phone: '',
    firstName: '',
    lastName: '',
    address: '',
    apartment: '',
    city: '',
    postalCode: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [orderComplete, setOrderComplete] = useState(false);
  const [orderNumber, setOrderNumber] = useState('');

  const subtotal = items.reduce((sum, item) => sum + item.price, 0);
  const shipping = 0; // Free shipping
  const total = subtotal + shipping;

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const target = e.target;
    
    // Always clear custom validity message when user types
    // This ensures the validation state resets properly
    target.setCustomValidity('');
    
    setFormData({
      ...formData,
      [target.name]: target.value,
    });
  };

  // Set custom validation messages in English
  const handleInvalid = (e: React.InvalidEvent<HTMLInputElement>) => {
    const target = e.target;
    
    if (target.validity.valueMissing) {
      target.setCustomValidity('Please fill out this field.');
    } else if (target.validity.typeMismatch) {
      if (target.type === 'email') {
        target.setCustomValidity('Please enter a valid email address.');
      } else if (target.type === 'tel') {
        target.setCustomValidity('Please enter a valid phone number.');
      }
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (items.length === 0) {
      alert('Your cart is empty');
      return;
    }

    setIsSubmitting(true);

    try {
      const orderRequest: OrderRequest = {
        customerName: `${formData.firstName} ${formData.lastName}`,
        customerEmail: formData.email,
        customerPhone: formData.phone,
        shippingAddress: `${formData.address}, ${formData.apartment ? formData.apartment + ', ' : ''}${formData.city} ${formData.postalCode}`,
        items: items.map(item => ({
          productId: item.id,
          quantity: 1,
        })),
      };

      const response = await orderApi.createOrder(orderRequest);

      if (response.success && response.data) {
        setOrderNumber(response.data.orderNumber);
        setOrderComplete(true);
        // Clear cart after successful order
        if (onOrderComplete) {
          onOrderComplete();
        }
      } else {
        alert('Order failed: ' + (response.message || 'Unknown error'));
      }
    } catch (error) {
      console.error('Order submission error:', error);
      alert('Failed to submit order. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen pt-24 pb-24 px-6 bg-[#F5F2EB] animate-fade-in-up">
      <div className="max-w-6xl mx-auto">
        <button
          onClick={onBack}
          className="group flex items-center gap-2 text-xs font-medium uppercase tracking-widest text-[#A8A29E] hover:text-[#2C2A26] transition-colors mb-12"
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-4 h-4 group-hover:-translate-x-1 transition-transform">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
          </svg>
          Back to Shop
        </button>

        {orderComplete ? (
          <div className="text-center py-20">
            <div className="w-20 h-20 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-6">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-10 h-10 text-green-600">
                <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
              </svg>
            </div>
            <h1 className="text-4xl font-serif text-[#2C2A26] mb-4">Order Confirmed!</h1>
            <p className="text-lg text-[#5D5A53] mb-2">Thank you for your order</p>
            <p className="text-sm text-[#A8A29E] mb-8">
              Order Number: <span className="font-semibold text-[#2C2A26]">{orderNumber}</span>
            </p>
            <button
              onClick={onBack}
              className="px-8 py-3 bg-[#2C2A26] text-[#F5F2EB] uppercase tracking-widest text-sm font-medium hover:bg-[#433E38] transition-colors"
            >
              Continue Shopping
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 lg:gap-24">

            {/* Left Column: Form */}
            <div>
              <h1 className="text-3xl font-serif text-[#2C2A26] mb-4">Checkout</h1>
              <p className="text-sm text-[#5D5A53] mb-12">Complete the form below to place your order.</p>

              <form onSubmit={handleSubmit} className="space-y-12">
                {/* Section 1: Contact */}
                <div>
                  <h2 className="text-xl font-serif text-[#2C2A26] mb-6">Contact Information</h2>
                  <div className="space-y-4">
                    <input
                      type="email"
                      name="email"
                      value={formData.email}
                      onChange={handleInputChange}
                      onInvalid={handleInvalid}
                      placeholder="Email address"
                      className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors"
                      required
                    />
                    <input
                      type="tel"
                      name="phone"
                      value={formData.phone}
                      onChange={handleInputChange}
                      onInvalid={handleInvalid}
                      placeholder="Phone number"
                      className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors"
                      required
                    />
                    <div className="flex items-center gap-2">
                      <input type="checkbox" id="newsletter" className="accent-[#2C2A26] cursor-not-allowed" disabled />
                      <label htmlFor="newsletter" className="text-sm text-[#5D5A53] cursor-not-allowed">Email me with news and offers</label>
                    </div>
                  </div>
                </div>

                {/* Section 2: Shipping */}
                <div>
                  <h2 className="text-xl font-serif text-[#2C2A26] mb-6">Shipping Address</h2>
                  <div className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <input
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleInputChange}
                        onInvalid={handleInvalid}
                        placeholder="First name"
                        className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors"
                        required
                      />
                      <input
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleInputChange}
                        onInvalid={handleInvalid}
                        placeholder="Last name"
                        className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors"
                        required
                      />
                    </div>
                    <input
                      type="text"
                      name="address"
                      value={formData.address}
                      onChange={handleInputChange}
                      onInvalid={handleInvalid}
                      placeholder="Address"
                      className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors autofill-fix"
                      required
                    />
                    <input
                      type="text"
                      name="apartment"
                      value={formData.apartment}
                      onChange={handleInputChange}
                      placeholder="Apartment, suite, etc. (optional)"
                      className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors"
                    />
                    <div className="grid grid-cols-2 gap-4">
                      <input
                        type="text"
                        name="city"
                        value={formData.city}
                        onChange={handleInputChange}
                        onInvalid={handleInvalid}
                        placeholder="City"
                        className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors"
                        required
                      />
                      <input
                        type="text"
                        name="postalCode"
                        value={formData.postalCode}
                        onChange={handleInputChange}
                        onInvalid={handleInvalid}
                        placeholder="Postal code"
                        className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors"
                        required
                      />
                    </div>
                  </div>
                </div>

                {/* Section 3: Payment (Mock) */}
                <div>
                  <h2 className="text-xl font-serif text-[#2C2A26] mb-6">Payment</h2>
                  <div className="p-6 border border-[#D6D1C7] bg-white/50 space-y-4">
                    <p className="text-sm text-[#5D5A53] mb-2">All transactions are secure and encrypted.</p>
                    <input type="text" placeholder="Card number" className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors cursor-not-allowed" disabled />
                    <div className="grid grid-cols-2 gap-4">
                      <input type="text" placeholder="Expiration (MM/YY)" className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors cursor-not-allowed" disabled />
                      <input type="text" placeholder="Security code" className="w-full bg-transparent border-b border-[#D6D1C7] py-3 text-[#2C2A26] placeholder-[#A8A29E] outline-none focus:border-[#2C2A26] transition-colors cursor-not-allowed" disabled />
                    </div>
                  </div>
                </div>

                <div>
                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="w-full py-5 bg-[#2C2A26] text-[#F5F2EB] uppercase tracking-widest text-sm font-medium hover:bg-[#433E38] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {isSubmitting ? 'Processing...' : `Pay Now — $${total}`}
                  </button>
                </div>
              </form>
            </div>

            {/* Right Column: Summary */}
            <div className="lg:pl-12 lg:border-l border-[#D6D1C7]">
              <h2 className="text-xl font-serif text-[#2C2A26] mb-8">Order Summary</h2>

              <div className="space-y-6 mb-8">
                {items.map((item, idx) => (
                  <div key={idx} className="flex gap-4">
                    <div className="w-16 h-16 bg-[#EBE7DE] relative">
                      <img src={item.imageUrl} alt={item.name} className="w-full h-full object-cover" />
                      <span className="absolute -top-2 -right-2 w-5 h-5 bg-[#2C2A26] text-white text-[10px] flex items-center justify-center rounded-full">1</span>
                    </div>
                    <div className="flex-1">
                      <h3 className="font-serif text-[#2C2A26] text-base">{item.name}</h3>
                      <p className="text-xs text-[#A8A29E]">{item.category}</p>
                    </div>
                    <span className="text-sm text-[#5D5A53]">${item.price}</span>
                  </div>
                ))}
              </div>

              <div className="border-t border-[#D6D1C7] pt-6 space-y-2">
                <div className="flex justify-between text-sm text-[#5D5A53]">
                  <span>Subtotal</span>
                  <span>${subtotal}</span>
                </div>
                <div className="flex justify-between text-sm text-[#5D5A53]">
                  <span>Shipping</span>
                  <span>Free</span>
                </div>
              </div>

              <div className="border-t border-[#D6D1C7] mt-6 pt-6">
                <div className="flex justify-between items-center">
                  <span className="font-serif text-xl text-[#2C2A26]">Total</span>
                  <div className="flex items-end gap-2">
                    <span className="text-xs text-[#A8A29E] mb-1">USD</span>
                    <span className="font-serif text-2xl text-[#2C2A26]">${total}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Checkout;