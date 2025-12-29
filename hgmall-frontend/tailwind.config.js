/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,ts,tsx,js,jsx}'],
  theme: {
    extend: {},
  },
  plugins: [require('daisyui')],
  daisyui: {
    themes: [
      {
        huigo: {
          primary: '#111827',
          'primary-content': '#ffffff',
          secondary: '#6b7280',
          accent: '#2563eb',
          neutral: '#1f2937',
          'base-100': '#ffffff',
          'base-200': '#f5f6f8',
          'base-300': '#e6e8ec',
          info: '#38bdf8',
          success: '#22c55e',
          warning: '#FFC400',
          'warning-content': '#1f2937',
          error: '#ef4444',
        },
      },
      'light',
      'dark',
    ],
  },
}


