"""Turn the Processing sketch (.pde) into a plain Java class so it can be tested.

This is a tiny version of what the Processing editor does behind the scenes:
- removes the Minim imports (the Drawing sketch never uses Minim),
- turns `color` variables into `int` (that is what `color` really is),
- moves size(...) into settings(), and makes the event methods public,
- wraps everything in `class DrawingProgram extends PApplet`.

Usage: python3 preprocess.py <sketch.pde> <output DrawingProgram.java>
"""
import re
import sys

src = open(sys.argv[1]).read()
src = re.sub(r"^import ddf\.minim.*$", "", src, flags=re.M)
src = re.sub(r"^color ", "int ", src, flags=re.M)
size_call = re.search(r"size\([^)]*\);", src).group(0)
src = src.replace(size_call, "", 1)
src = re.sub(r"^void (setup|draw|keyPressed|mousePressed)\(\)", r"public void \1()", src, flags=re.M)
java = (
    "import processing.core.*;\n"
    "public class DrawingProgram extends PApplet {\n"
    f"public void settings() {{ {size_call} }}\n"
    f"{src}\n"
    "}\n"
)
open(sys.argv[2], "w").write(java)
