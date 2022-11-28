/** @type {import('tailwindcss').Config} */
module.exports =  {
  mode: "jit",
  content: ["./src/main/resources/templates/**/*.{html,js}",  "./node_modules/flowbite/**/*.js"],
  theme: {
    extend: {},
  },
  variants : {
    extend : {},
  },
  plugins: [
    require('flowbite/plugin')
  ],
}
