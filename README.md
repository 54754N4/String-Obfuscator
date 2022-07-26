# String Obfuscator
Obfuscates strings using a polymorphic engine. Creates a chain of transformations which are then reversed to generate the decryption routine. Each time its ran it generates a unique routine.

## Language Targets

- [x] Java
- [x] C#
- [x] C & C++
- [x] Python
- [x] JS
- [x] PowerShell
- [x] Bash
- [x] MASM (64 bit)

## Java-based Generation
The engine can be configured to generate a random number of transformation (in a specified range), as well in how many bits to encode the data:
```java
int minOps = 5;    // minimum number of transformations
int maxOps = 10;   // maximum number of transformations
int maxBits = 16;  // encodes UTF-16 data
PolymorphicEngine engine = new PolymorphicEngine(minOps, maxOps, maxBits);
```

Then generate a transformation chain and encoded data by calling the `Engine#transform` method. To generate the decryption routine in a specific target language, just use the correct `Visitor` class:
```java
String stringToObfuscate = "Hello World!";
Context ctx = engine.transform(stringToObfuscate);
JavaVisitor visitor = new JavaVisitor();  // or any other target
System.out.println(visitor.visit(ctx));
```

## API Endpoints Generation

After running the spring boot application, each target can be accessed through the `/obfuscate/{target}` api url path, where `{target}` can be any of the following: c, cpp, csharp, java, javascript, python, masm64, bash, powershell. The Swagger UI can also be accessed using this URL `<host>:1337/swagger-ui/index.html`.
Each endpoint takes two optional parameters to specify the range `[minOps, maxOps]` that the polymorphic engine will use to generate transformations.

## C & C++ Target Example
```c
wchar_t string[12] = {0xe7e5,0xef5f,0xee64,0xee64,0xeeac,0xe6d7,0xef9e,0xeeac,0xef24,0xee64,0xef57,0xe6df};
for (unsigned int qiTILYFPbT=0, OzYohOKxe, ZNtEkIj; qiTILYFPbT < 12; qiTILYFPbT++) {
	OzYohOKxe = string[qiTILYFPbT];
	ZNtEkIj = ((OzYohOKxe >> 0x4) ^ (OzYohOKxe >> 0x2)) & ((1 << 0x2) - 1);
	OzYohOKxe ^= (ZNtEkIj << 0x4) | (ZNtEkIj << 0x2);
	OzYohOKxe -= 0x2c16;
	OzYohOKxe = (((OzYohOKxe & 0xffff) << 0x7) | (OzYohOKxe >> 0x9)) & 0xffff;
	OzYohOKxe = (((OzYohOKxe & 0xffff) >> 0x5) | (OzYohOKxe << 0xb)) & 0xffff;
	ZNtEkIj = ((OzYohOKxe >> 0xb) ^ (OzYohOKxe >> 0x2)) & ((1 << 0x2) - 1);
	OzYohOKxe ^= (ZNtEkIj << 0xb) | (ZNtEkIj << 0x2);
	OzYohOKxe = (((OzYohOKxe & 0xffff) >> 0x7) | (OzYohOKxe << 0x9)) & 0xffff;
	OzYohOKxe -= 0xdb6;
	string[qiTILYFPbT] = OzYohOKxe;
}
wprintf(string);
```
## C# Target Example
```csharp
var str = new System.Text.StringBuilder("\u69c5\u4f85\u79a5\u79a5\u65a5\u4b65\u45c5\u65a5\u41a5\u79a5\u5b85\u7d65");
for (int dSouLvBYK=0, ZFKLohnHzM, TnaNQlKjk; dSouLvBYK < str.Length; dSouLvBYK++) {
	ZFKLohnHzM = str[dSouLvBYK];
	TnaNQlKjk = ((ZFKLohnHzM >> 0x5) ^ (ZFKLohnHzM >> 0xa)) & ((1 << 0x4) - 1);
	ZFKLohnHzM ^= (TnaNQlKjk << 0x5) | (TnaNQlKjk << 0xa);
	ZFKLohnHzM = (((ZFKLohnHzM & 0xffff) << 0xb) | (ZFKLohnHzM >> 0x5)) & 0xffff;
	ZFKLohnHzM += 0x2d3d;
	TnaNQlKjk = ((ZFKLohnHzM >> 0x3) ^ (ZFKLohnHzM >> 0x3)) & ((1 << 0x9) - 1);
	ZFKLohnHzM ^= (TnaNQlKjk << 0x3) | (TnaNQlKjk << 0x3);
	ZFKLohnHzM ^= 0xc60e;
	ZFKLohnHzM -= 0x118d;
	ZFKLohnHzM ^= 0x1bbb;
	ZFKLohnHzM ^= 0x6970;
	ZFKLohnHzM = ~ZFKLohnHzM & 0xffff;
	str[dSouLvBYK] = (char) ZFKLohnHzM;
}
Console.WriteLine(str);
```
## Java Target Example
```java
StringBuilder string = new StringBuilder("\u2be6\uabec\uebfe\uebfe\uabe0\uabf7\ue7f1\uabe0\u6bfa\uebfe\uabee\uabf5");
for (int LuLHaKFh=0, oY_FOvLH, eebCvtC; LuLHaKFh < string.length(); LuLHaKFh++) {
    oY_FOvLH = string.charAt(LuLHaKFh);
    oY_FOvLH = (((oY_FOvLH & 0xffff) << 0x6) | (oY_FOvLH >> 0xa)) & 0xffff;
    eebCvtC = ((oY_FOvLH >> 0x0) ^ (oY_FOvLH >> 0x7)) & ((1 << 0x4) - 1);
    oY_FOvLH ^= (eebCvtC << 0x0) | (eebCvtC << 0x7);
    oY_FOvLH ^= 0x600f;
    oY_FOvLH -= 0x2aa2;
    oY_FOvLH += 0x25b0;
    oY_FOvLH ^= 0x9852;
    string.setCharAt(LuLHaKFh, (char) oY_FOvLH);
}
System.out.println(string);
```
## Python Target Example
```python
string = [0xbaec,0x3b6a,0xbbee,0xbbee,0x3be9,0xbb08,0x3b6d,0x3be9,0xbbeb,0xbbee,0xbb6a,0x3b08]
for ehNxDDeClr in range(len(string)):
	JvYJM = string[ehNxDDeClr]
	JvYJM = ~JvYJM & 0xffff
	rukl = ((JvYJM >> 0x7) ^ (JvYJM >> 0x3)) & ((1 << 0x2) - 1)
	JvYJM ^= (rukl << 0x7) | (rukl << 0x3)
	JvYJM = (((JvYJM & 0xffff) << 0x2) | (JvYJM >> 0xe)) & 0xffff
	JvYJM ^= 0x4f69
	JvYJM -= 0x179c
	rukl = ((JvYJM >> 0x6) ^ (JvYJM >> 0x6)) & ((1 << 0x3) - 1)
	JvYJM ^= (rukl << 0x6) | (rukl << 0x6)
	JvYJM = (((JvYJM & 0xffff) >> 0x1) | (JvYJM << 0xf)) & 0xffff
	JvYJM -= 0x217c
	string[ehNxDDeClr] = chr(JvYJM & 0xffff)
del ehNxDDeClr, JvYJM, rukl
string = ''.join(string)
print(string)
```
## JavaScript Target Example
```js
var string = [0x346e,0x22ee,0x266e,0x266e,0x27ee,0x6e,0x3bee,0x27ee,0x296e,0x266e,0x226e,0xee];
for (var LeKvHT=0, bpbEN; LeKvHT < string.length; LeKvHT++) {
	bpbEN = string[LeKvHT];
	bpbEN = (((bpbEN & 0xffff) << 0xb) | (bpbEN >> 0x5)) & 0xffff;
	bpbEN = ~bpbEN & 0xffff;
	bpbEN = (((bpbEN & 0xffff) << 0xf) | (bpbEN >> 0x1)) & 0xffff;
	bpbEN = ~bpbEN & 0xffff;
	bpbEN ^= 0xb841;
	bpbEN = (((bpbEN & 0xffff) >> 0x1) | (bpbEN << 0xf)) & 0xffff;
	string[LeKvHT] = bpbEN;
}
string = String.fromCodePoint(...string);
console.log(string);
```
## PowerShell Target Example
```powershell
[uint64[]]$HZaRySkFpz = 0x67b1,0xf791,0xd7b1,0xd7b1,0xc7d1,0x7b1,0x27d1,0xc7d1,0xb7f1,0xd7b1,0xf7b1,0x791
$string = [System.Text.StringBuilder]::new()
for ($ONsGU = 0; $ONsGU -lt $HZaRySkFpz.Length; $ONsGU++) {
	$XKvkUzFdZ = $HZaRySkFpz[$ONsGU]
	$XKvkUzFdZ = ((($XKvkUzFdZ -band 0xffff) -shl 0x2) -bor ($XKvkUzFdZ -shr 0xe)) -band 0xffff
	$XKvkUzFdZ = ((($XKvkUzFdZ -band 0xffff) -shr 0xf) -bor ($XKvkUzFdZ -shl 0x1)) -band 0xffff
	$QCjLahqh = (($XKvkUzFdZ -shr 0x7) -bxor ($XKvkUzFdZ -shr 0xc)) -band ((1 -shl 0x3) - 1)
	$XKvkUzFdZ = $XKvkUzFdZ -bxor (($QCjLahqh -shl 0x7) -bor ($QCjLahqh -shl 0xc))
	$XKvkUzFdZ = ((($XKvkUzFdZ -band 0xffff) -shr 0xc) -bor ($XKvkUzFdZ -shl 0x4)) -band 0xffff
	$XKvkUzFdZ = -bnot $XKvkUzFdZ -band 0xffff
	$XKvkUzFdZ = ((($XKvkUzFdZ -band 0xffff) -shl 0xf) -bor ($XKvkUzFdZ -shr 0x1)) -band 0xffff
	$XKvkUzFdZ += 0x2282
	$QCjLahqh = (($XKvkUzFdZ -shr 0xa) -bxor ($XKvkUzFdZ -shr 0xb)) -band ((1 -shl 0x4) - 1)
	$XKvkUzFdZ = $XKvkUzFdZ -bxor (($QCjLahqh -shl 0xa) -bor ($QCjLahqh -shl 0xb))
	$XKvkUzFdZ = $XKvkUzFdZ -bxor 0x4a60
	[void]$string.Append([char]($XKvkUzFdZ -band 0xffff))
}
$XKvkUzFdZ = [void]$XKvkUzFdZ
$ONsGU = [void]$ONsGU
$HZaRySkFpz = [void]$HZaRySkFpz
$QCjLahqh = [void]$QCjLahqh
$string = $string.ToString()
Write-Host $string
```
## Bash Target Example
```bash
string=( 0x8cfa 0x873c 0x8b3a 0x8b3a 0x8a3c 0x89ba 0x861c 0x8a3c 0x885a 0x8b3a 0x873a 0x89bc )
for bEkkOsPjN in ${!string[@]}; do
	xC_adWJDY=${string[$bEkkOsPjN]}
	((yLexvN = ((xC_adWJDY >> 0x3) ^ (xC_adWJDY >> 0x3)) & ((1 << 0xb)-1)))
	((xC_adWJDY ^= (yLexvN << 0x3) | (yLexvN << 0x3)))
	((xC_adWJDY = (((xC_adWJDY & 0xffff) << 0xc) | (xC_adWJDY >> 0x4)) & 0xffff))
	((xC_adWJDY = (((xC_adWJDY & 0xffff) << 0xa) | (xC_adWJDY >> 0x6)) & 0xffff))
	((xC_adWJDY += 0x243c))
	((xC_adWJDY = (((xC_adWJDY & 0xffff) << 0x9) | (xC_adWJDY >> 0x7)) & 0xffff))
	((xC_adWJDY -= 0x186d))
	((xC_adWJDY += 0x396b))
	((xC_adWJDY -= 0xc3b))
	((yLexvN = ((xC_adWJDY >> 0x1) ^ (xC_adWJDY >> 0x7)) & ((1 << 0x3)-1)))
	((xC_adWJDY ^= (yLexvN << 0x1) | (yLexvN << 0x7)))
	((xC_adWJDY ^= 0xd246))
	string[$bEkkOsPjN]=$xC_adWJDY
done
unset bEkkOsPjN
unset xC_adWJDY
unset yLexvN
string=$(printf %b "$(printf '\\U%x' "${string[@]}")")
echo $string
```
## MASM 64 bit Target Example
```asm
extern GetStdHandle: proc
extern WriteFile: proc
extern GetFileType: proc
extern WriteConsoleW: proc

.data?
	stdout	dq ?
	written	dq ?
.data
	string dw 0c5d7h,0afd8h,0b457h,0b457h,0add7h,065d8h,0edd6h,0add7h,0c756h,0b457h,0b458h,06f58h
	len	equ $-string
.code
main proc
	push	rbp
	mov	rbp, rsp
	sub	rsp, 32
	and	rsp, -10h

	mov	rbx, offset string
	xor	rcx, rcx
Qjlc:
	xor	rax, rax
	xor	rdx, rdx
	xor	r8, r8
	xor	r9, r9
	xor	r10, r10
	mov	dx, word ptr [rbx + rcx*2]
	mov	r8w, dx
	shr	r8w, 7
	mov	r9w, dx
	shr	r9w, 10
	xor	r8w, r9w
	mov	r9w, 1
	shl	r9w, 2
	sub	r9w, 1
	and	r8w, r9w
	mov	r9w, r8w
	shl	r9w, 7
	mov	r10w, r8w
	shl	r10w, 10
	or	r9w, r10w
	xor	dx, r9w
	sub	dx, 14630
	ror	dx, 13
	ror	dx, 14
	mov	r8w, dx
	shr	r8w, 5
	mov	r9w, dx
	shr	r9w, 0
	xor	r8w, r9w
	mov	r9w, 1
	shl	r9w, 2
	sub	r9w, 1
	and	r8w, r9w
	mov	r9w, r8w
	shl	r9w, 5
	mov	r10w, r8w
	shl	r10w, 0
	or	r9w, r10w
	xor	dx, r9w
	sub	dx, 293
	not	dx
	rol	dx, 3
	xor	dx, 22228
	mov	word ptr [rbx + rcx*2], dx
	inc	cx
	cmp	cx, 12
	jne	Qjlc

	; Printing code
	xor	rax, rax
	xor	rcx, rcx
	xor	rdx, rdx
	xor	r8, r8
	xor	r9, r9
	mov	rcx, -11
	call	GetStdHandle
	mov	[stdout], rax
	mov	rcx, rax
	call	GetFileType
	cmp	rax, 1
	je	fileWrite
	mov	rcx, [stdout]
	mov	rdx, rbx
	mov	r8, len
	mov	r9, written
	call	WriteConsoleW
	jmp	epilog
fileWrite:
	mov	rcx, [stdout]
	mov	rdx, rbx
	mov	r8, len
	mov	r9, written
	call	WriteFile
epilog:
	add	rsp, 32
	mov	rsp, rbp
	pop	rbp
	ret
main endp
end
```
