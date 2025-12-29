#import "@preview/cheq:0.3.0":checklist
#import "@preview/zebraw:0.1.0": * 

#let template(it) = {
   // First set the text style
  //  set text(lang:"zh",font:("Libertinus Serif","Noto Serif CJK SC"))
  
  set text(lang:"zh",
   font:("Times New Roman","Microsoft YaHei")
  )
   // Then set the page style
   set page(
    numbering:(..x)=>context 
    {
      str(x.pos().first()) + "/" + str(counter(page).final().first()) 
    }
   )
   // Set the headings
   set heading(numbering: "1.")
   // Set the level 1 headings
   show heading.where(level:1): it => {
    // Set the text style for the heading
    set text(size:20pt)
    align(center,it)
   }
   // Import the checklist function to show todo lists
   show :checklist
   show : zebraw.with()
   // Set the code style
  //  show raw : set text(font: ("DejaVu Sans Mono","Noto Sans Mono CJK SC"))
  show raw : set text(
    font: ("JetBrains Mono", "Noto Sans Mono CJK SC")
  )
   // Set the link style
   show link: set text(fill:blue.lighten(20%))
   show link: underline
  show table: align.with(center)
  it
}

#let work(..names) = {
  text(fill:orange)[#names.pos().join(" & ") working on]
}

#let done(..names) = {
  text(fill.green.darken(10%))[✓ by #names.pos().join(" &") ]
}

#let waiting = {
  text(fill:red,weight:"bold")[⏳]
}

