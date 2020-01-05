rm main-bundle.js
rm main-bundle.css
echo $(curl https://web3.space/wallet-main-bundle.js > main-bundle.js)
echo $(curl https://web3.space/wallet-main-bundle.css > main-bundle.css)

