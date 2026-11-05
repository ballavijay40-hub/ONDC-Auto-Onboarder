/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        ondc: {
          blue: '#1E40AF',
          navy: '#0F172A',
          teal: '#0D9488',
          accent: '#2563EB',
          light: '#F8FAFC'
        }
      }
    },
  },
  plugins: [],
}

