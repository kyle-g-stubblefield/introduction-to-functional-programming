#!/bin/bash
set -e

echo "🔧 Setting up Clojure FP Classroom environment..."

# Install Clojure CLI
echo "📦 Installing Clojure CLI..."
curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh
chmod +x linux-install.sh
sudo ./linux-install.sh
rm linux-install.sh

# Install shadow-cljs globally
echo "📦 Installing shadow-cljs..."
npm install -g shadow-cljs

# Install project npm deps
echo "📦 Installing project dependencies..."
npm install

# Pre-warm the shadow-cljs/JVM cache so first `npm run dev` is fast
echo "☕ Warming JVM cache (30s, only happens once)..."
npx shadow-cljs classpath > /dev/null 2>&1 || true

echo ""
echo "✅ Environment ready!"
echo ""
echo "  npm run dev   → start dev server on port 8080"
echo "  npm run test  → run tests"
