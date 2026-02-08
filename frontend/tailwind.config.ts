import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: '#0969DA',
        'primary-hover': '#0550AE',
        border: '#E5E7EB',
        'bg-subtle': '#F6F8FA',
        'text-primary': '#1F2937',
        'text-secondary': '#6B7280',
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
}

export default config
