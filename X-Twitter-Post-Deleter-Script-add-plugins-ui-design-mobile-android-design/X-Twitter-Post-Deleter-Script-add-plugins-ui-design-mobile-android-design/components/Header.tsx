
import React from 'react';

const Header: React.FC = () => {
  return (
    <header className="text-center border-b border-gray-700 pb-6">
      <h1 className="text-4xl sm:text-5xl font-bold text-white">
        X Post Deleter Script
      </h1>
      <p className="mt-4 text-lg text-gray-400">
        An toàn và tự động xóa tất cả bài đăng của bạn trên X (Twitter).
      </p>
    </header>
  );
};

export default Header;
