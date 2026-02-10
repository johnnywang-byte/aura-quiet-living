/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */


import React, { useState, useRef, useEffect } from 'react';
import { ChatMessage } from '../types';
import { chatApi, ChatRequest } from '../services/api';

interface AssistantProps {
    isOpen: boolean;
    onToggle: () => void;
}

const Assistant: React.FC<AssistantProps> = ({ isOpen, onToggle }) => {
    const [messages, setMessages] = useState<ChatMessage[]>([
        { role: 'model', text: 'Welcome to Aura. I am here to help you find objects that resonate with your life. How may I assist?', timestamp: Date.now() }
    ]);
    const [inputValue, setInputValue] = useState('');
    const [isThinking, setIsThinking] = useState(false);
    const [sessionId, setSessionId] = useState<string>('');
    const [width, setWidth] = useState(540);
    const [height, setHeight] = useState(700);
    const [isResizing, setIsResizing] = useState(false);
    const scrollRef = useRef<HTMLDivElement>(null);
    const chatBoxRef = useRef<HTMLDivElement>(null);
    const resizeStartRef = useRef<{ width: number; height: number; mouseX: number; mouseY: number; corner: string } | null>(null);

    useEffect(() => {
        if (scrollRef.current) {
            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
        }
    }, [messages, isOpen]);

    // Handle resize
    const handleResizeStart = (e: React.MouseEvent, corner: string) => {
        e.preventDefault();
        e.stopPropagation();
        setIsResizing(true);
        resizeStartRef.current = {
            width,
            height,
            mouseX: e.clientX,
            mouseY: e.clientY,
            corner
        };
    };

    useEffect(() => {
        const handleMouseMove = (e: MouseEvent) => {
            if (!isResizing || !resizeStartRef.current) return;

            const { width: startWidth, height: startHeight, mouseX, mouseY, corner } = resizeStartRef.current;
            const deltaX = e.clientX - mouseX;
            const deltaY = e.clientY - mouseY;

            let newWidth = startWidth;
            let newHeight = startHeight;

            // Calculate new dimensions based on which corner is being dragged
            if (corner.includes('right')) {
                newWidth = startWidth + deltaX;
            } else if (corner.includes('left')) {
                newWidth = startWidth - deltaX;
            }

            if (corner.includes('bottom')) {
                newHeight = startHeight + deltaY;
            } else if (corner.includes('top')) {
                newHeight = startHeight - deltaY;
            }

            // Apply constraints
            newWidth = Math.max(380, Math.min(newWidth, window.innerWidth * 0.9));
            newHeight = Math.max(500, Math.min(newHeight, window.innerHeight * 0.85));

            setWidth(newWidth);
            setHeight(newHeight);
        };

        const handleMouseUp = () => {
            setIsResizing(false);
            resizeStartRef.current = null;
        };

        if (isResizing) {
            document.addEventListener('mousemove', handleMouseMove);
            document.addEventListener('mouseup', handleMouseUp);
        }

        return () => {
            document.removeEventListener('mousemove', handleMouseMove);
            document.removeEventListener('mouseup', handleMouseUp);
        };
    }, [isResizing, width, height]);

    const handleSend = async () => {
        if (!inputValue.trim()) return;

        const userMsg: ChatMessage = { role: 'user', text: inputValue, timestamp: Date.now() };
        setMessages(prev => [...prev, userMsg]);
        setInputValue('');
        setIsThinking(true);

        try {
            const request: ChatRequest = {
                message: userMsg.text,
                sessionId: sessionId
            };

            const response = await chatApi.sendMessage(request);

            if (response) {
                if (response.sessionId) {
                    setSessionId(response.sessionId);
                }

                const aiMsg: ChatMessage = {
                    role: 'model',
                    text: response.message,
                    timestamp: Date.now()
                };
                setMessages(prev => [...prev, aiMsg]);
            } else {
                const errorMsg: ChatMessage = {
                    role: 'model',
                    text: "I'm having trouble connecting to the server. Please try again later.",
                    timestamp: Date.now()
                };
                setMessages(prev => [...prev, errorMsg]);
            }

        } catch (error) {
            console.error("Chat Error:", error);
            const errorMsg: ChatMessage = {
                role: 'model',
                text: "An error occurred. Please check your connection.",
                timestamp: Date.now()
            };
            setMessages(prev => [...prev, errorMsg]);
        } finally {
            setIsThinking(false);
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            handleSend();
        }
    };

    // Resize handle component
    const ResizeHandle = ({ corner }: { corner: string }) => {
        const position: React.CSSProperties = {};
        let cursor = 'nwse-resize';

        if (corner === 'bottom-right') {
            position.bottom = 0;
            position.right = 0;
            cursor = 'nwse-resize';
        } else if (corner === 'bottom-left') {
            position.bottom = 0;
            position.left = 0;
            cursor = 'nesw-resize';
        } else if (corner === 'top-right') {
            position.top = 0;
            position.right = 0;
            cursor = 'nesw-resize';
        } else if (corner === 'top-left') {
            position.top = 0;
            position.left = 0;
            cursor = 'nwse-resize';
        }

        return (
            <div
                onMouseDown={(e) => handleResizeStart(e, corner)}
                style={{
                    position: 'absolute',
                    width: '16px',
                    height: '16px',
                    cursor,
                    zIndex: 10,
                    ...position
                }}
                className="hover:bg-[#A8A29E]/20 transition-colors"
            >
                <svg
                    width="16"
                    height="16"
                    viewBox="0 0 16 16"
                    className="text-[#A8A29E]"
                >
                    {corner === 'bottom-right' && (
                        <path d="M14 14L14 10M14 14L10 14" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
                    )}
                    {corner === 'bottom-left' && (
                        <path d="M2 14L2 10M2 14L6 14" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
                    )}
                    {corner === 'top-right' && (
                        <path d="M14 2L14 6M14 2L10 2" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
                    )}
                    {corner === 'top-left' && (
                        <path d="M2 2L2 6M2 2L6 2" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
                    )}
                </svg>
            </div>
        );
    };

    return (
        <div className="fixed bottom-8 right-8 z-50 flex flex-col items-end font-sans">
            {isOpen && (
                <div
                    ref={chatBoxRef}
                    className="bg-[#F5F2EB] rounded-none shadow-2xl shadow-[#2C2A26]/10 mb-6 flex flex-col overflow-hidden border border-[#D6D1C7] animate-slide-up-fade relative"
                    style={{
                        width: window.innerWidth <= 640 ? '90vw' : `${width}px`,
                        height: `${height}px`
                    }}
                >
                    {/* Resize Handles */}
                    <ResizeHandle corner="top-left" />
                    <ResizeHandle corner="top-right" />
                    <ResizeHandle corner="bottom-left" />
                    <ResizeHandle corner="bottom-right" />

                    {/* Header */}
                    <div className="bg-[#EBE7DE] p-5 border-b border-[#D6D1C7] flex justify-between items-center">
                        <div className="flex items-center gap-3">
                            <div className="w-2 h-2 bg-[#2C2A26] rounded-full animate-pulse"></div>
                            <span className="font-serif italic text-[#2C2A26] text-lg">Concierge</span>
                        </div>
                        <button onClick={onToggle} className="text-[#A8A29E] hover:text-[#2C2A26] transition-colors">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1} stroke="currentColor" className="w-6 h-6">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>

                    {/* Chat Area */}
                    <div className="flex-1 overflow-y-auto p-6 space-y-8 bg-[#F5F2EB]" ref={scrollRef}>
                        {messages.map((msg, idx) => (
                            <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                                <div
                                    className={`max-w-[85%] p-5 text-base leading-relaxed ${msg.role === 'user'
                                        ? 'bg-[#2C2A26] text-[#F5F2EB]'
                                        : 'bg-white border border-[#EBE7DE] text-[#5D5A53] shadow-sm'
                                        }`}
                                >
                                    {msg.text}
                                </div>
                            </div>
                        ))}
                        {isThinking && (
                            <div className="flex justify-start">
                                <div className="bg-white border border-[#EBE7DE] p-5 flex gap-1 items-center shadow-sm">
                                    <div className="w-1.5 h-1.5 bg-[#A8A29E] rounded-full animate-bounce"></div>
                                    <div className="w-1.5 h-1.5 bg-[#A8A29E] rounded-full animate-bounce delay-75"></div>
                                    <div className="w-1.5 h-1.5 bg-[#A8A29E] rounded-full animate-bounce delay-150"></div>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Input Area */}
                    <div className="p-5 bg-[#F5F2EB] border-t border-[#D6D1C7]">
                        <div className="flex gap-2 relative">
                            <input
                                type="text"
                                value={inputValue}
                                onChange={(e) => setInputValue(e.target.value)}
                                onKeyDown={handleKeyPress}
                                placeholder="Ask anything..."
                                className="flex-1 bg-white border border-[#D6D1C7] focus:border-[#2C2A26] px-4 py-3 text-base outline-none transition-colors placeholder-[#A8A29E] text-[#2C2A26]"
                            />
                            <button
                                onClick={handleSend}
                                disabled={!inputValue.trim() || isThinking}
                                className="bg-[#2C2A26] text-[#F5F2EB] px-4 hover:bg-[#444] transition-colors disabled:opacity-50"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-5 h-5">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
                                </svg>
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <div className="relative group">
                {/* 背景智能光晕：模拟 AI 待命状态的能量场 */}
                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-32 h-32 bg-[#F5F2EB]/10 blur-3xl rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-1000" />

                <button
                    onClick={onToggle}
                    className={`
            relative z-10 flex items-center justify-center rounded-full shadow-2xl 
            /* 尺寸调整：大尺寸圆形 */
            w-24 h-24 
            /* 背景与毛玻璃：增加材质感 */
            bg-[#2C2A26]/90 backdrop-blur-md 
            /* 文字颜色与边框 */
            text-[#F5F2EB] border border-[#F5F2EB]/20 
            /* 丝滑过渡：使用 duration-700 配合 cubic-bezier */
            transition-all duration-700 ease-[cubic-bezier(0.23,1,0.32,1)]
            /* 交互效果 */
            hover:scale-110 hover:border-[#F5F2EB]/60 hover:shadow-[0_0_40px_rgba(245,242,235,0.2)]
            active:scale-95
        `}
                    aria-label="Toggle AI Assistant"
                >
                    {/* 内部呼吸微光层 */}
                    <div className="absolute inset-0 rounded-full bg-[#F5F2EB]/5 animate-pulse" />

                    {isOpen ? (
                        /* 关闭图标：降低描边粗细以显得更高端 */
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 0 24 24"
                            strokeWidth={1}
                            stroke="currentColor"
                            className="w-10 h-10 relative z-20 opacity-90"
                        >
                            <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
                        </svg>
                    ) : (
                        /* "Ai" 文字：增加 tracking-widest 实现呼吸感 */
                        <span className="relative z-20 font-serif italic text-3xl tracking-[0.1em] ml-1 opacity-95">
                            Ai
                        </span>
                    )}
                </button>
            </div>
        </div>
    );
};

export default Assistant;
