/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
*/


import React, { useState, useEffect } from 'react';
import { BRAND_NAME } from '../constants';

interface NavbarProps {
  onNavClick: (e: React.MouseEvent<HTMLAnchorElement>, targetId: string) => void;
  cartCount: number;
  onOpenCart: () => void;
  onOpenChat?: () => void;
}

const Navbar: React.FC<NavbarProps> = ({ onNavClick, cartCount, onOpenCart, onOpenChat }) => {
  const [scrolled, setScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 50);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleLinkClick = (e: React.MouseEvent<HTMLAnchorElement>, targetId: string) => {
    setMobileMenuOpen(false);
    onNavClick(e, targetId);
  };

  const handleCartClick = (e: React.MouseEvent) => {
    e.preventDefault();
    setMobileMenuOpen(false);
    onOpenCart();
  }

  // Determine text color based on state
  const textColorClass = (scrolled || mobileMenuOpen) ? 'text-[#2C2A26]' : 'text-[#F5F2EB]';

  return (
    <>
      <nav
        className={`fixed top-0 left-0 right-0 z-50 transition-all duration-700 ease-in-out ${scrolled || mobileMenuOpen ? 'bg-[#F5F2EB]/90 backdrop-blur-md py-4 shadow-sm' : 'bg-transparent py-8'
          }`}
      >
        <div className="max-w-[1800px] mx-auto px-8 flex items-center justify-between">
          {/* Logo */}
          <a
            href="#"
            onClick={(e) => {
              e.preventDefault();
              window.scrollTo({ top: 0, behavior: 'smooth' });
              onNavClick(e, ''); // Pass empty string to just reset to home
            }}
            className={`text-3xl font-serif font-medium tracking-tight z-50 relative transition-colors duration-500 ${textColorClass}`}
          >
            {BRAND_NAME}
          </a>

          {/* Center Links - Desktop */}
          <div className={`hidden md:flex items-center gap-12 text-sm font-medium tracking-widest uppercase transition-colors duration-500 ${textColorClass}`}>
            <a href="#products" onClick={(e) => handleLinkClick(e, 'products')} className="hover:opacity-60 transition-opacity">Shop</a>
            <a href="#about" onClick={(e) => handleLinkClick(e, 'about')} className="hover:opacity-60 transition-opacity">About</a>
            <a href="#journal" onClick={(e) => handleLinkClick(e, 'journal')} className="hover:opacity-60 transition-opacity">Journal</a>
          </div>

          {/* Right Actions */}
          <div className={`flex items-center gap-6 z-50 relative transition-colors duration-500 ${textColorClass}`}>
            {onOpenChat && (
              <button
                onClick={() => onOpenChat()}
                className="text-sm font-medium uppercase tracking-widest hover:opacity-60 transition-opacity hidden sm:flex items-center gap-2"
                title="AI Assistant"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 8.715L18 9.75l-.259-1.035a3.375 3.375 0 00-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 002.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 002.456 2.456L21.75 6l-1.035.259a3.375 3.375 0 00-2.456 2.456zM16.894 20.567L16.5 21.75l-.394-1.183a2.25 2.25 0 00-1.423-1.423L13.5 18.75l1.183-.394a2.25 2.25 0 001.423-1.423l.394-1.183.394 1.183a2.25 2.25 0 001.423 1.423l1.183.394-1.183.394a2.25 2.25 0 00-1.423 1.423z" />
                </svg>
                AI
              </button>
            )}
            <button
              onClick={handleCartClick}
              className="text-sm font-medium uppercase tracking-widest hover:opacity-60 transition-opacity hidden sm:block"
            >
              Cart ({cartCount})
            </button>

            {/* Mobile Menu Toggle */}
            <button
              className={`block md:hidden focus:outline-none transition-colors duration-500 ${textColorClass}`}
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            >
              {mobileMenuOpen ? (
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-6 h-6">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              ) : (
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-6 h-6">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
                </svg>
              )}
            </button>
          </div>
        </div>
      </nav>

      {/* Mobile Menu Overlay */}
      <div className={`fixed inset-0 bg-[#F5F2EB] z-40 flex flex-col justify-center items-center transition-all duration-500 ease-in-out ${mobileMenuOpen ? 'opacity-100 translate-y-0 pointer-events-auto' : 'opacity-0 -translate-y-10 pointer-events-none'
        }`}>
        <div className="flex flex-col items-center space-y-8 text-xl font-serif font-medium text-[#2C2A26]">
          <a href="#products" onClick={(e) => handleLinkClick(e, 'products')} className="hover:opacity-60 transition-opacity">Shop</a>
          <a href="#about" onClick={(e) => handleLinkClick(e, 'about')} className="hover:opacity-60 transition-opacity">About</a>
          <a href="#journal" onClick={(e) => handleLinkClick(e, 'journal')} className="hover:opacity-60 transition-opacity">Journal</a>
          <button
            onClick={handleCartClick}
            className="hover:opacity-60 transition-opacity text-base uppercase tracking-widest font-sans mt-8"
          >
            Cart ({cartCount})
          </button>
        </div>
      </div>
    </>
  );
};

export default Navbar;
