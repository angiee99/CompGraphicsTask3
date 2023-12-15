# Scratch model of 3D scene
This lab includes implementing the visual pipeline, rasterization of 3D 
objects, applying model, view and projection transformations. 

## Functionality 

- Switch between active objects by pressing Period key (.)
- To active object you can apply : 
  - translation (left/right, up/down with respective keys, 
move farther and closer with keys L and M)
  - rotation around the axis (keys X, Y, Z)
  - zoom (key Equals (=) for increasing, key Minus (-) for decreasing)
  - animation in time (key E)
- Drag the mouse to move the scene
- Use mouse wheel to move forward and backwards in the scene
- Use keys WSAD for moving forward, backwards, left, right
- Change the projection mode 
  - key O - orthogonal
  - key P - perspective
- Change the sharpness of cubic curves by pressing
  - *curve key + shift to increase sharpness 
  - *curve key + alt/option to decrease sharpness
- Hide or show the object by pressing its object/curve key
### Object keys:
- C - Cube
- G - Pyramid
- R - Prism
- H - Ferguson surface
- T - Cosin curve
### Curve keys:
- F - Ferguson (yellow)
- B - Bezier (azure)
- N - Coons (red)