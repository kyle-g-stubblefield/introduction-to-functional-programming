# Programming 2 — Functional Game Project

A ClojureScript game built with pure functional principles.
The architecture mirrors Racket's `big-bang` from HtDP.

## Getting Started

```bash
npm run dev
```

Then open the browser preview on **port 8080**.  
Save any `.cljs` file and the browser updates automatically (hot reload).

---

## Architecture

```
src/game/
  core.cljs   ← entry point, wires everything together (don't edit often)
  state.cljs  ← pure functions: all game logic lives here
  draw.cljs   ← side effects: rendering only, reads state but never changes it

test/game/
  state_test.cljs  ← tests for every function in state.cljs
```

### The big-bang pattern

| Racket (`big-bang`)  | This project          |
|----------------------|-----------------------|
| `on-tick`            | `state/update-state`  |
| `to-draw`            | `draw/draw-state`     |
| `on-key`             | `state/handle-key`    |
| initial state        | `state/initial-state` |

### The rule

**`state.cljs` must stay pure.** No `q/` calls, no `js/`, no side effects.  
**`draw.cljs` only reads state.** It never calls `swap!` or modifies anything.

This separation is the whole point. If you keep it, your game logic becomes
trivially testable and the rendering becomes trivially replaceable.

---

## HtDP Design Recipe

Before writing any new function, fill in this template as a comment:

```clojure
;; Data definition: what does this function take and return?
;;   A Foo is a map: {:x Number :y Number}

;; Signature:
;;   my-function : Foo Number -> Foo

;; Examples:
;;   (my-function {:x 0 :y 0} 5) => {:x 5 :y 0}

;; Then write the test in state_test.cljs, THEN implement.
```

---

## Commands

| Command         | What it does                        |
|-----------------|-------------------------------------|
| `npm run dev`   | Start dev server with hot reload    |
| `npm run test`  | Run all tests                       |
| `npm run build` | Production build to `public/js/`    |
| `npm run repl`  | Connect a browser REPL via Calva    |

## Controls (default)

| Key         | Action         |
|-------------|----------------|
| `←` `→`    | Move           |
| `↑`         | Jump           |
| `space`     | Stop           |
| `p`         | Pause/unpause  |
| `r`         | Reset          |

---

## Adding Features

New game features follow this pattern every time:

1. **Update the data definition** — does the state map need a new key?
2. **Write a pure function** in `state.cljs` that transforms state
3. **Write the test first** in `state_test.cljs`
4. **Wire input** — add a `cond` branch in `handle-key` if needed
5. **Add rendering** in `draw.cljs` if it needs to be visible

The architecture stays the same no matter how complex the game gets.
