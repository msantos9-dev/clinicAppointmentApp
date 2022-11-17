/** @type {import('tailwindcss').Config} */
const withMT = require("@material-tailwind/html/utils/withMT");
module.exports =  withMT( {
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
})
