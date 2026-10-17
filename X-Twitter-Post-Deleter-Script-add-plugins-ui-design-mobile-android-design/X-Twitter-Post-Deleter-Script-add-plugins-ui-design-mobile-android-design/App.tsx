
import React from 'react';
import Header from './components/Header';
import InstructionsPanel from './components/InstructionsPanel';
import ScriptPanel from './components/ScriptPanel';
import Footer from './components/Footer';

const App: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-900 text-gray-200 flex flex-col items-center p-4 sm:p-6 lg:p-8">
      <div className="w-full max-w-4xl mx-auto">
        <Header />
        <main className="mt-8 space-y-8">
          <InstructionsPanel />
          <ScriptPanel />
        </main>
        <Footer />
      </div>
    </div>
  );
};

export default App;
