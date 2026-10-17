import React, { useState } from 'react';
import CopyIcon from './icons/CopyIcon';
import CheckIcon from './icons/CheckIcon';

const ScriptPanel: React.FC = () => {
  const [copied, setCopied] = useState(false);

  const scriptContent = `
(async function() {
    'use strict';

    // --- Configuration ---
    // QUAN TRỌNG: Hãy thay thế các giá trị dưới đây bằng tên hiển thị và @username của bạn!
    // Bạn có thể thêm nhiều tên nếu bạn đã từng thay đổi chúng.
    const myDisplayNames = ['YOUR_DISPLAY_NAME_HERE']; // Ví dụ: ['Elon Musk', 'Elon Tusk']
    const myUsernames = ['@YOUR_USERNAME_HERE'];       // Ví dụ: ['@elonmusk']
    
    const config = {
        // Delays in milliseconds
        delayActionMin: 500,
        delayActionMax: 2000,
        delayAfterDeleteMin: 800,
        delayAfterDeleteMax: 2500,
        delayBatchMin: 30000,
        delayBatchMax: 90000,

        // Batch settings
        batchSizeMin: 10,
        batchSizeMax: 20,

        // Retries
        maxRetries: 3,

        // Selectors (these might need updating if X.com changes its UI)
        tweetSelector: 'article[data-testid="tweet"]',
        userInfoSelector: '[data-testid="User-Name"]',
        moreButtonSelector: '[data-testid="caret"]',
        deleteMenuItemSelector: '[role="menuitem"]',
        confirmDeleteButtonSelector: '[data-testid="confirmationSheetConfirm"]',
    };

    // --- State ---
    window.stopDeleting = false;
    let deletedCount = 0;
    let batchCount = 0;
    let currentBatchSize = Math.floor(Math.random() * (config.batchSizeMax - config.batchSizeMin + 1)) + config.batchSizeMin;

    // --- Helper Functions ---
    const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));
    const randomDelay = (min, max) => Math.floor(Math.random() * (max - min + 1)) + min;
    
    const log = (message, type = 'info') => {
        const styles = {
            info: 'background: #3B82F6; color: #FFFFFF; padding: 2px 6px; border-radius: 4px;',
            warn: 'background: #F59E0B; color: #000000; padding: 2px 6px; border-radius: 4px;',
            error: 'background: #EF4444; color: #FFFFFF; padding: 2px 6px; border-radius: 4px;',
            success: 'background: #10B981; color: #FFFFFF; padding: 2px 6px; border-radius: 4px;',
            skip: 'background: #6B7280; color: #FFFFFF; padding: 2px 6px; border-radius: 4px;'
        };
        console.log(\`%c[X-Deleter] %c\${message}\`, 'font-weight: bold;', styles[type] || styles.info);
    };

    async function findElementWithText(selector, textOptions, context = document) {
        for (let i = 0; i < config.maxRetries * 2; i++) {
            const elements = Array.from(context.querySelectorAll(selector));
            for (const text of textOptions) {
                const targetElement = elements.find(el => el.textContent.trim().toLowerCase() === text.toLowerCase());
                if (targetElement) return targetElement;
            }
            await sleep(500);
        }
        return null;
    }
    
    // --- Pre-run Check ---
    if (myDisplayNames[0] === 'YOUR_DISPLAY_NAME_HERE' || myUsernames[0] === '@YOUR_USERNAME_HERE') {
        log('LỖI: Bạn chưa cấu hình tên hiển thị (myDisplayNames) và/hoặc tên người dùng (myUsernames) ở đầu script. Vui lòng cập nhật thông tin và chạy lại.', 'error');
        return;
    }

    // --- Main Logic ---
    log('Script started. To stop, type "window.stopDeleting = true" in the console.', 'info');
    
    while (!window.stopDeleting) {
        const tweets = document.querySelectorAll(\`\${config.tweetSelector}:not([data-x-deleter-processed="true"])\`);

        if (tweets.length === 0) {
            log('No more tweets found. Scrolling down to load more...', 'info');
            window.scrollBy(0, window.innerHeight * 2);
            await sleep(3000);

            if (document.querySelectorAll(\`\${config.tweetSelector}:not([data-x-deleter-processed="true"])\`).length === 0) {
                log('No new tweets loaded. Assuming all your posts are deleted. Script finished.', 'success');
                break;
            }
            continue;
        }

        const tweet = tweets[0];
        tweet.setAttribute('data-x-deleter-processed', 'true');
        
        const userInfoEl = tweet.querySelector(config.userInfoSelector);
        if (!userInfoEl) {
            log('Could not find user info block in tweet. Skipping.', 'warn');
            tweet.style.border = "2px solid #6B7280";
            continue;
        }

        const displayName = userInfoEl.querySelector('div > div > a > div > div > span > span')?.textContent?.trim();
        const username = userInfoEl.querySelector('div > div > a > div > div:nth-child(2) > div > span')?.textContent?.trim();

        const isMyTweet = myDisplayNames.some(name => name.toLowerCase() === displayName?.toLowerCase()) || 
                          myUsernames.some(name => name.toLowerCase() === username?.toLowerCase());

        if (!isMyTweet) {
            log(\`Skipping tweet from \${username || displayName}. Not yours.\`, 'skip');
            tweet.style.border = "2px solid #6B7280";
            continue;
        }

        log(\`Found your post/reply. Preparing to delete tweet #\${deletedCount + 1}...\`, 'info');
        tweet.style.border = "2px solid #F59E0B";

        const moreButton = tweet.querySelector(config.moreButtonSelector);
        if (!moreButton) {
            log('Could not find "More" button. Skipping tweet.', 'error');
            tweet.style.border = "2px solid #EF4444";
            continue;
        }
        moreButton.click();
        await sleep(randomDelay(config.delayActionMin, config.delayActionMax));

        const deleteMenuItem = await findElementWithText(config.deleteMenuItemSelector, ['Delete', 'Xóa'], document);
        if (!deleteMenuItem) {
            log('Could not find "Delete" menu item. Skipping tweet.', 'error');
            tweet.style.border = "2px solid #EF4444";
            document.body.click(); // Attempt to close the menu
            await sleep(500);
            continue;
        }
        deleteMenuItem.click();
        await sleep(randomDelay(config.delayActionMin, config.delayActionMax));

        let confirmButton = await findElementWithText(config.confirmDeleteButtonSelector, ['Delete', 'Xóa'], document);
        if (!confirmButton) {
            confirmButton = await findElementWithText('[role="button"]', ['Delete', 'Xóa'], document);
        }
        
        if (!confirmButton) {
            log('Could not find "Confirm Delete" button. Skipping tweet.', 'error');
            tweet.style.border = "2px solid #EF4444";
            document.body.click(); // Attempt to close dialog
            await sleep(500);
            continue;
        }
        confirmButton.click();
        
        await sleep(randomDelay(config.delayAfterDeleteMin, config.delayAfterDeleteMax));

        deletedCount++;
        batchCount++;
        log(\`Successfully deleted post #\${deletedCount}.\`, 'success');
        
        if (window.stopDeleting) {
            log('Stop signal received. Halting script.', 'warn');
            break;
        }

        if (batchCount >= currentBatchSize) {
            const breakDuration = randomDelay(config.delayBatchMin, config.delayBatchMax);
            log(\`Batch of \${batchCount} deleted. Taking a long break for \${Math.round(breakDuration / 1000)} seconds...\`, 'info');
            await sleep(breakDuration);
            batchCount = 0;
            currentBatchSize = Math.floor(Math.random() * (config.batchSizeMax - config.batchSizeMin + 1)) + config.batchSizeMin;
            log('Break finished. Starting new batch.', 'info');
        }
    }

    if (!window.stopDeleting) {
        log('Script completed! All visible posts belonging to you have been processed.', 'success');
    }
})();
  `.trim();

  const handleCopy = () => {
    navigator.clipboard.writeText(scriptContent).then(() => {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    });
  };

  return (
    <div className="bg-gray-800 border border-gray-700 rounded-lg">
      <div className="flex justify-between items-center p-4 border-b border-gray-700">
        <h2 className="text-xl font-semibold text-white">Deletion Script</h2>
        <button
          onClick={handleCopy}
          className="flex items-center space-x-2 bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200"
          aria-label={copied ? 'Script copied' : 'Copy script'}
        >
          {copied ? <CheckIcon /> : <CopyIcon />}
          <span>{copied ? 'Đã sao chép!' : 'Sao chép Script'}</span>
        </button>
      </div>

      <div className="p-4 bg-yellow-900/30 border-b border-gray-700 text-yellow-200" role="alert">
        <p className="font-semibold flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor"><path fillRule="evenodd" d="M8.257 3.099c.636-1.21 2.852-1.21 3.488 0l6.066 11.578c.636 1.21-.472 2.723-1.744 2.723H3.934c-1.272 0-2.38-1.513-1.744-2.723L8.257 3.099zM10 6a.75.75 0 01.75.75v3.5a.75.75 0 01-1.5 0v-3.5A.75.75 0 0110 6zm0 8a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" /></svg>
          Chú ý quan trọng
        </p>
        <p className="mt-2 text-sm text-yellow-300">
          Sau khi dán script vào console, bạn <strong>BẮT BUỘC</strong> phải chỉnh sửa 2 dòng đầu tiên (`myDisplayNames` và `myUsernames`) để nó chỉ xóa các bài đăng của bạn. Nếu không, script sẽ báo lỗi và không chạy.
        </p>
      </div>

      <div className="p-4 bg-gray-900 rounded-b-lg overflow-x-auto">
        <pre className="text-sm text-gray-300">
          <code>{scriptContent}</code>
        </pre>
      </div>
    </div>
  );
};

export default ScriptPanel;