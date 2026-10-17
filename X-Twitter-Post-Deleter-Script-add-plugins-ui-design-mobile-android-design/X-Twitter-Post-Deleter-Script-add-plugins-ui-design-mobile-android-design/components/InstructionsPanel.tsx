import React from 'react';

const InstructionsPanel: React.FC = () => {
  return (
    <div className="bg-gray-800 border border-gray-700 rounded-lg p-6">
      <h2 className="text-2xl font-semibold text-white mb-4">Làm thế nào để sử dụng</h2>
      <ol className="list-decimal list-inside space-y-3 text-gray-300">
        <li>
          Truy cập trang hồ sơ X (Twitter) của bạn trên trình duyệt (ví dụ: `https://x.com/your_username`).
        </li>
        <li>
          Mở Bảng điều khiển dành cho nhà phát triển bằng cách nhấn <kbd className="px-2 py-1.5 text-xs font-semibold text-gray-200 bg-gray-700 border border-gray-600 rounded-lg">F12</kbd> hoặc <kbd className="px-2 py-1.5 text-xs font-semibold text-gray-200 bg-gray-700 border border-gray-600 rounded-lg">Ctrl+Shift+I</kbd> (Windows/Linux) hoặc <kbd className="px-2 py-1.5 text-xs font-semibold text-gray-200 bg-gray-700 border border-gray-600 rounded-lg">Cmd+Opt+I</kbd> (Mac).
        </li>
        <li>
          Nhấp vào tab "Console".
        </li>
        <li>
          Sao chép toàn bộ script từ ô bên dưới.
        </li>
        <li>
          Dán script vào console. <strong>QUAN TRỌNG:</strong> Trước khi nhấn Enter, hãy tìm và thay đổi 2 dòng `myDisplayNames` và `myUsernames` ở đầu script để khớp với thông tin tài khoản của bạn.
        </li>
        <li>
          Sau khi đã chỉnh sửa, nhấn <kbd className="px-2 py-1.5 text-xs font-semibold text-gray-200 bg-gray-700 border border-gray-600 rounded-lg">Enter</kbd> để bắt đầu.
        </li>
        <li>
          Để dừng script bất cứ lúc nào, hãy gõ <code className="bg-gray-700 text-red-400 p-1 rounded">window.stopDeleting = true;</code> vào console và nhấn <kbd className="px-2 py-1.5 text-xs font-semibold text-gray-200 bg-gray-700 border border-gray-600 rounded-lg">Enter</kbd>.
        </li>
      </ol>
    </div>
  );
};

export default InstructionsPanel;