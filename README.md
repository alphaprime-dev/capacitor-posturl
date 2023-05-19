# capacitor-posturl

capacitor-posturl

## Install

```bash
npm install capacitor-posturl
npx cap sync
```

## API

<docgen-index>

- [`posturl(...)`](#posturl)
- [Interfaces](#interfaces)
- [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### posturl(...)

```typescript
posturl(data: PostData) => Promise<void>
```

| Param      | Type                                          |
| ---------- | --------------------------------------------- |
| **`data`** | <code><a href="#postdata">PostData</a></code> |

---

### Interfaces

#### PostData

| Prop          | Type                                                           |
| ------------- | -------------------------------------------------------------- |
| **`url`**     | <code>string</code>                                            |
| **`body`**    | <code><a href="#record">Record</a>&lt;string,string&gt;</code> |
| **`headers`** | <code><a href="#record">Record</a>&lt;string,string&gt;</code> |

### Type Aliases

#### Record

Construct a type with a set of properties K of type T

<code>{
[P in K]: T;
}</code>

</docgen-api>
