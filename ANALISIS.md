# Analisis Aplikasi ToDo List Android

> **Dibuat**: 29 Juni 2026

---

## Daftar Isi

1. [Gambaran Umum](#1-gambaran-umum)
2. [Teknologi yang Digunakan](#2-teknologi-yang-digunakan)
3. [Struktur Direktori](#3-struktur-direktori)
4. [Fitur Lengkap](#4-fitur-lengkap)
5. [Model Database (Room)](#5-model-database-room)
6. [Relasi Antar Tabel](#6-relasi-antar-tabel)
7. [Arsitektur Aplikasi](#7-arsitektur-aplikasi)
8. [Alur Navigasi](#8-alur-navigasi)
9. [Third-Party Libraries](#9-third-party-libraries)
10. [Riwayat Pengembangan](#10-riwayat-pengembangan)
11. [Catatan Penting](#11-catatan-penting)

---

## 1. Gambaran Umum

Ini adalah aplikasi **To-Do List Android native** (bukan web app) dengan nama proyek `todolist` dan application ID `com.example.todolist`. Aplikasi sudah **100% selesai** dengan 12 commits dari inisialisasi hingga fitur final.

Aplikasi ini menyimpan **semua data tugas dan kategori di SQLite lokal** (Room Database). Hanya fitur **autentikasi yang menggunakan Firebase** (cloud). Tidak ada backend server sendiri — murni aplikasi client-side.

---

## 2. Teknologi yang Digunakan

| Lapisan | Teknologi | Versi |
|---|---|---|
| **Bahasa Pemrograman** | Kotlin | 2.1.10 |
| **Build System** | Gradle + Android Gradle Plugin (AGP) | 8.5 / 8.3.2 |
| **Min SDK** | Android 7.0 (Nougat) — API 24 | |
| **Target SDK** | Android 14 — API 34 | |
| **Compiler** | KSP (Kotlin Symbol Processing) | 2.1.10-1.0.29 |
| **Database Lokal** | Room (SQLite ORM dari Android Jetpack) | 2.6.1 |
| **Autentikasi** | Firebase Authentication | Firebase BoM 33.1.0 |
| **UI Toolkit** | Material Design 3 (Material Components) | 1.11.0 |
| **Arsitektur** | **MVVM** (Model-View-ViewModel) + Repository Pattern | |
| **Navigation** | Fragment-based + BottomNavigationView | |
| **Java Compatibility** | Java 17 | |
| **AndroidX** | Core KTX, AppCompat, Activity, Fragment | |

---

## 3. Struktur Direktori

```
final-project/
│
├── build.gradle.kts                  # Root Gradle — plugin deps
├── settings.gradle.kts               # Project settings + repo (Google, Maven, Jitpack)
├── gradle.properties                 # Gradle JVM config (AndroidX, Jetifier)
├── PLAN.md                           # Dokumentasi progres fitur
├── google-services.json              # Firebase config (dalam folder app)
│
└── app/
    ├── build.gradle.kts              # Dependencies aplikasi
    ├── google-services.json
    └── src/main/
        ├── AndroidManifest.xml
        │
        ├── java/com/example/todolist/
        │   │
        │   ├── model/                        # ❖ ENTITY / DATA MODEL
        │   │   ├── Task.kt                   #    Entity tugas
        │   │   ├── Category.kt               #    Entity kategori
        │   │   ├── User.kt                   #    Entity user (local backup)
        │   │   └── TaskWithCategory.kt       #    Relasi Task ↔ Category
        │   │
        │   ├── database/                     # ❖ DATABASE & DAOs
        │   │   ├── AppDatabase.kt            #    Room DB + Seeder
        │   │   ├── TaskDao.kt                #    Query tugas
        │   │   ├── CategoryDao.kt            #    Query kategori
        │   │   └── UserDao.kt                #    Query user
        │   │
        │   ├── repository/                   # ❖ REPOSITORY
        │   │   └── TodoRepository.kt         #    Single source of truth
        │   │
        │   ├── viewmodel/                    # ❖ VIEWMODEL
        │   │   ├── TaskViewModel.kt          #    Logic tugas & kategori
        │   │   ├── AuthViewModel.kt          #    ⚠️ Masih stub (kosong)
        │   │   └── ProfileViewModel.kt       #    ⚠️ Masih stub (kosong)
        │   │
        │   ├── view/                         # ❖ UI LAYER (Activities)
        │   │   ├── LoginActivity.kt          #    Halaman login
        │   │   ├── RegisterActivity.kt       #    Halaman registrasi
        │   │   ├── MainActivity.kt           #    Main + Bottom Nav
        │   │   ├── AddTaskActivity.kt        #    Tambah tugas
        │   │   ├── EditTaskActivity.kt       #    Edit tugas
        │   │   ├── TaskDetailActivity.kt     #    Detail tugas
        │   │   ├── ManageCategoriesActivity.kt #   Kelola kategori
        │   │   └── fragment/                 #    ❖ FRAGMENTS
        │   │       ├── HomeFragment.kt       #      Daftar tugas
        │   │       ├── CalendarFragment.kt   #      Kalender
        │   │       └── ProfileFragment.kt    #      Profil user
        │   │
        │   ├── adapter/                      # ❖ RECYCLERVIEW ADAPTERS
        │   │   ├── TaskAdapter.kt            #    Adapter daftar tugas
        │   │   ├── CategoryAdapter.kt        #    Adapter daftar kategori
        │   │   └── CategoryBadgeAdapter.kt   #    Adapter badge kategori
        │   │
        │   └── utils/                        # ❖ UTILITY
        │       └── ThemeStorage.kt           #    Theme manager (SharedPrefs)
        │
        └── res/
            ├── layout/                       # 15 file XML layout
            ├── drawable/                     # Icons, shapes, user.png
            ├── menu/                         # bottom_nav_menu.xml
            ├── color/                        # nav_item_color.xml
            └── values/
                ├── colors.xml                # Palet warna tema
                ├── strings.xml               # String resources
                └── themes.xml                # 4 tema (Purple/Blue/Green/Pink)
```

---

## 4. Fitur Lengkap

### 4.1 🔐 Autentikasi (Firebase Auth)

| Fitur | Lokasi | Status |
|---|---|---|
| Register dengan email + password + username | `RegisterActivity.kt` | ✅ |
| Validasi format email | `RegisterActivity.kt` | ✅ |
| Validasi password minimal 6 karakter | `RegisterActivity.kt` | ✅ |
| Login dengan email & password | `LoginActivity.kt` | ✅ |
| Remember Me (checkbox) | `LoginActivity.kt` (SharedPreferences) | ✅ |
| Auto-login jika Remember Me aktif | `LoginActivity.kt` | ✅ |
| Logout + hapus session | `ProfileFragment.kt` | ✅ |
| Edit nama profil | `ProfileFragment.kt` → Firebase `updateProfile()` | ✅ |
| Edit foto profil (dari galeri) | `ProfileFragment.kt` → `PickVisualMedia` | ✅ |
| Ganti password | `ProfileFragment.kt` → Firebase `updatePassword()` | ✅ |

### 4.2 📋 Manajemen Tugas (Full CRUD)

| Fitur | Lokasi | Detail |
|---|---|---|
| **Tambah Tugas** | `AddTaskActivity.kt` | Title (required), description, deadline (DatePicker), priority (dropdown), category (dropdown) |
| **Edit Tugas** | `EditTaskActivity.kt` | Update semua field; data di-load via `getTaskById()` |
| **Hapus Tugas** | `TaskAdapter.kt` | Delete + Snackbar **Undo** (cancel delete) |
| **Detail Tugas** | `TaskDetailActivity.kt` | Layar khusus: title, category badge warna, priority, deadline, description |
| **Filter by Kategori** | `HomeFragment.kt` | Badge horizontal kategori — klik untuk filter, klik lagi untuk reset |
| **Filter by Priority** | `HomeFragment.kt` | Spinner: Semua Prioritas / Low / Medium / High |
| **Filter Kombinasi** | `HomeFragment.kt` | Kategori + Priority bisa aktif bersamaan |
| **Search Real-time** | `HomeFragment.kt` | Search by title via `LIKE %query%` (TextWatcher) |
| **Checkbox Selesai** | `TaskAdapter.kt` | Tandai isDone = true/false, update langsung ke DB |
| **Hapus Semua Selesai** | `HomeFragment.kt` | Dialog konfirmasi → `DELETE WHERE isDone = 1` |
| **Snackbar Undo Delete** | `TaskAdapter.kt` | Muncul 3 detik, bisa undo dengan insert ulang |
| **DiffUtil Optimization** | `TaskAdapter.kt` | `TaskDiffCallback` untuk animasi list yang efisien |

### 4.3 🏷️ Manajemen Kategori (Full CRUD)

| Fitur | Detail |
|---|---|
| **Tambah Kategori** | Input nama + palet 18 warna interaktif (lingkaran) |
| **Edit Kategori** | Update nama & warna |
| **Hapus Kategori** | Dialog konfirmasi — tugas di kategori itu **tidak ikut terhapus** (SET_NULL) |
| **Badge Kategori** | Horizontal scrollable badges di Home — warna solid + teks kontras otomatis |
| **Warna Dinamis** | Setiap kategori punya warna sendiri; digunakan di card tugas sebagai strip kiri |

### 4.4 📅 Kalender Interaktif

| Fitur | Detail |
|---|---|
| **Library** | `MaterialCalendarView` (prolificinteractive) |
| **Dot Markers** | Circle merah pada tanggal yang memiliki tugas (via `DayViewDecorator`) |
| **Filter by Date** | Klik tanggal → tampilkan tugas dengan deadline sesuai |
| **Progress Bar Harian** | Progress bar + persentase `completed/total` |
| **Motivational Emoji** | 🎉 100%, 🚀 76-99%, 🔥 51-75%, 🙂 26-50%, 😴 < 26% |
| **Support Multi-Format** | Tanggal otomatis dinormalisasi dari 3 format: `dd MMMM yyyy` (ID/EN) dan `yyyy-MM-dd` |

### 4.5 🎨 Multi-Theme (4 Warna)

| Tema | Warna Primary | Style |
|---|---|---|
| **Purple** (default) | `#6C63FF` | `Theme.ToDoList` |
| **Blue** | `#2196F3` | `Theme.ToDoList.Blue` |
| **Green** | `#4CAF50` | `Theme.ToDoList.Green` |
| **Pink** | `#E91E63` | `Theme.ToDoList.Pink` |

- Tema dipilih dari halaman Profile
- Disimpan di `SharedPreferences` (`theme_prefs`)
- Diterapkan via `setTheme()` SEBELUM `super.onCreate()`
- Perubahan tema langsung trigger `recreate()` activity

### 4.6 🧭 Bottom Navigation

| Tab | Fragment | Ikon |
|---|---|---|
| **Tasks** | `HomeFragment` | `ic_tasks.xml` |
| **Calendar** | `CalendarFragment` | `ic_calendar_custom.xml` |
| **Profile** | `ProfileFragment` | `ic_profile_custom.xml` |

- Navigasi menggunakan `FragmentTransaction.replace()` di dalam satu `MainActivity`
- Default fragment: `HomeFragment`

### 4.7 🌱 Database Seeder

Saat pertama kali database dibuat (`onCreate`), langsung diisi:
- **4 Kategori**: Work (#4285F4), Personal (#EA4335), Shopping (#FBBC05), Study (#34A853)
- **8 Contoh Tugas** dengan berbagai prioritas, deadline, kategori, dan status selesai

---

## 5. Model Database (Room)

### 5.1 Entity: `tasks`

| Kolom | Tipe | Constraint | Default | Keterangan |
|---|---|---|---|---|
| `id` | `INT` | `@PrimaryKey autoGenerate` | 0 | ID unik tugas |
| `title` | `TEXT` | NOT NULL | — | Judul tugas (required) |
| `description` | `TEXT` | — | `""` | Deskripsi opsional |
| `deadline` | `TEXT` | — | `""` | Deadline, format `dd MMMM yyyy` (locale ID) |
| `priority` | `TEXT` | — | `""` | Prioritas: `Low` / `Medium` / `High` |
| `categoryId` | `INT?` | FK → `categories.id` ON DELETE `SET_NULL` | `null` | Kategori tugas (nullable) |
| `isDone` | `BOOLEAN` | — | `false` | Status selesai |

```sql
CREATE TABLE tasks (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    title       TEXT NOT NULL,
    description TEXT DEFAULT '',
    deadline    TEXT DEFAULT '',
    priority    TEXT DEFAULT '',
    categoryId  INTEGER REFERENCES categories(id) ON DELETE SET_NULL,
    isDone      INTEGER DEFAULT 0  -- boolean
);
CREATE INDEX index_tasks_categoryId ON tasks(categoryId);
```

### 5.2 Entity: `categories`

| Kolom | Tipe | Constraint | Keterangan |
|---|---|---|---|
| `id` | `INT` | `@PrimaryKey autoGenerate` | ID unik kategori |
| `name` | `TEXT` | NOT NULL | Nama kategori |
| `color` | `TEXT` | NOT NULL | Hex color: `"#6C63FF"` |

### 5.3 Entity: `users` (untuk local backup)

| Kolom | Tipe | Constraint | Keterangan |
|---|---|---|---|
| `id` | `INT` | `@PrimaryKey autoGenerate` | ID unik user |
| `username` | `TEXT` | NOT NULL | Username |
| `email` | `TEXT` | NOT NULL | Email |
| `password` | `TEXT` | NOT NULL | Password |

> **Catatan**: Entity `User` ada di database lokal tapi **autentikasi sebenarnya via Firebase**. Ini mungkin sisa dari development awal sebelum Firebase diintegrasikan.

---

## 6. Relasi Antar Tabel

```
┌──────────────┐       ┌──────────────────┐
│  categories  │       │     tasks        │
├──────────────┤       ├──────────────────┤
│ id (PK)      │──┐    │ id (PK)          │
│ name         │  └──→ │ categoryId (FK)  │  ON DELETE SET_NULL
│ color        │       │ title            │
└──────────────┘       │ description      │
                       │ deadline         │
┌──────────────┐       │ priority         │
│    users     │       │ isDone           │
├──────────────┤       └──────────────────┘
│ id (PK)      │
│ username     │       ┌──────────────────────────────┐
│ email        │       │    TaskWithCategory (Relasi)  │
│ password     │       ├──────────────────────────────┤
└──────────────┘       │ @Embedded: Task              │
                       │ @Relation: Category?          │
                       └──────────────────────────────┘
```

- Satu kategori bisa memiliki banyak tugas (one-to-many)
- Jika kategori dihapus, `categoryId` di tugas menjadi `null` (data tugas tetap aman)
- `TaskWithCategory` adalah POJO relasi yang menggabungkan Task + Category-nya dalam satu query (`@Transaction`)

---

## 7. Arsitektur Aplikasi

### 7.1 MVVM + Repository Pattern

```
┌──────────┐     ┌────────────┐     ┌──────────────┐     ┌─────────┐     ┌──────────┐
│  View /  │ ←─→ │ ViewModel  │ ←─→ │  Repository  │ ←─→ │   DAO   │ ←─→ │ Room DB  │
│ Fragment │     │            │     │              │     │         │     │ (SQLite) │
└──────────┘     └────────────┘     └──────────────┘     └─────────┘     └──────────┘
      │                │                    │                   │
      │           viewModelScope       Single Source          Interface
      │           .launch {}          of Truth                Room Query
 User Input        coroutine                                 suspend fun
 Events              ⬇                                          ⬇
              LiveData<List<T>>                           LiveData / suspend
```

### 7.2 Alur Data (Contoh: Tambah Tugas)

```
1. [AddTaskActivity] user klik "Save"
2.   → viewModel.insertTask(task)
3.     → viewModelScope.launch { repository.insertTask(task) }
4.       → taskDao.insertTask(task)  [suspend function]
5.         → Room menulis ke SQLite
6.           → LiveData allTasks otomatis ter-update
7.             → [HomeFragment / CalendarFragment] observe dan render ulang
```

### 7.3 Pembagian Layer

| Layer | Tanggung Jawab | File |
|---|---|---|
| **Model** | Entity Room (data class) | `Task.kt`, `Category.kt`, `User.kt` |
| **DAO** | Interface query SQL | `TaskDao.kt`, `CategoryDao.kt`, `UserDao.kt` |
| **Database** | Room DB + seeding | `AppDatabase.kt` |
| **Repository** | Single source of truth | `TodoRepository.kt` |
| **ViewModel** | Jembatan View ↔ Repository | `TaskViewModel.kt` |
| **View** | Activity, Fragment, Adapter | Semua di folder `view/` dan `adapter/` |

### 7.4 Catatan Arsitektur

- **AuthViewModel** dan **ProfileViewModel** masih **stub (kosong)** — logika autentikasi ditulis langsung di Activity/Fragment
- Semua operasi database menggunakan **coroutines** (`suspend fun`)
- Observe data menggunakan **LiveData** (bukan Flow)
- `TaskViewModel` adalah `AndroidViewModel` (punya akses ke `Application` context)

---

## 8. Alur Navigasi

```
┌──────────────────┐
│  LoginActivity   │  ←── Launcher Activity (MAIN/LAUNCHER)
│  (Login/Regis.)  │
└────────┬─────────┘
         │ login / auto-login success
         ▼
┌──────────────────┐
│   MainActivity   │  ←── Bottom Navigation (Single Activity)
│                  │
│  ┌─ HomeFragment ─┐  Daftar tugas + filter + search
│  │  (Tasks)       │
│  └────────────────┘
│       │ klik task        klik +FAB    klik category
│       ▼                   ▼                   ▼
│  TaskDetailActivity  AddTaskActivity  ManageCategoriesActivity
│       │                                    │
│       └──→ EditTaskActivity                │
│                                            │
│  ┌─ CalendarFragment ┐  Kalender + tugas by date
│  │  (Calendar)       │
│  └───────────────────┘
│       │ klik +FAB → AddTaskActivity
│
│  ┌─ ProfileFragment ─┐  Profil, tema, logout
│  │  (Profile)        │
│  └───────────────────┘
│       │ ganti tema → recreate()
│       │ logout → LoginActivity + finish()
│
└──────────────────┘
```

---

## 9. Third-Party Libraries

| Library | Group/Artifact | Fungsi | Sumber |
|---|---|---|---|
| **Firebase Authentication** | `com.google.firebase:firebase-auth` | Login/Register via email | Google Maven via BoM |
| **Firebase Analytics** | `com.google.firebase:firebase-analytics` | Pelacakan event (via BoM) | Google Maven via BoM |
| **Room Runtime** | `androidx.room:room-runtime` | ORM SQLite | Google Maven |
| **Room KTX** | `androidx.room:room-ktx` | Coroutines support untuk Room | Google Maven |
| **Room Compiler** (KSP) | `androidx.room:room-compiler` | Code generation Room | Google Maven |
| **Material Components** | `com.google.android.material:material` | Material 3 UI | Google Maven |
| **MaterialCalendarView** | `com.github.prolificinteractive:material-calendarview` | Kalender interaktif | Jitpack |
| **AndroidX Core KTX** | `androidx.core:core-ktx` | Kotlin extensions | Google Maven |
| **AndroidX AppCompat** | `androidx.appcompat:appcompat` | Backward compatibility | Google Maven |
| **Activity KTX** | `androidx.activity:activity-ktx` | Activity + ViewModels | Google Maven |
| **Fragment KTX** | `androidx.fragment:fragment-ktx` | Fragment + ViewModels | Google Maven |
| **Lifecycle ViewModel KTX** | `androidx.lifecycle:lifecycle-viewmodel-ktx` | ViewModel coroutines | Google Maven |
| **Lifecycle LiveData KTX** | `androidx.lifecycle:lifecycle-livedata-ktx` | LiveData coroutines | Google Maven |
| **Lifecycle Runtime KTX** | `androidx.lifecycle:lifecycle-runtime-ktx` | Lifecycle-aware coroutines | Google Maven |

**Tidak ada** backend server, REST API, atau cloud database lain selain Firebase Auth.

---

## 10. Riwayat Pengembangan

Berdasarkan 12 commits dari git log:

| No | Commit | Deskripsi |
|---|---|---|
| 1 | `cbb842c` | 🆕 **init project** — Inisialisasi project Android |
| 2 | `95321b5` | 🚧 masih banyak bug sama masih banyak kurang |
| 3 | `44fd5f3` | ✨ **feat: todolist feature and authentication** |
| 4 | `a994c36` | ✨ **feat: task category crud and bottom navbar** |
| 5 | `17bc7d2` | ✨ **feat: task category label and calendar feature** |
| 6 | `7c382a2` | 🐛 fix: update overall UI and functionality |
| 7 | `320a204` | indahh |
| 8 | `70375f4` | Menambahkan theme color, tambah edit profile, sama progress task |
| 9 | `8522ddc` | perbaiki warna temanya sama halaman profile |
| 10 | `65553a1` | perbaiki Halaman kalender |
| 11 | `ec7372b` | perbaiki bug profile |
| 12 | `53306d8` | ✅ **perbaiki bug** (commit terakhir) |

---

## 11. Catatan Penting

### ⚠️ Temuan

1. **AuthViewModel & ProfileViewModel masih stub** — logika auth ditulis langsung di Activity/Fragment, tidak melalui ViewModel seperti arsitektur MVVM seharusnya.

2. **Entity `User` ada tapi tidak dipakai** — `UserDao` dan entity `User` ada di database lokal, tapi autentikasi menggunakan Firebase Auth. Mungkin sisa dari development awal.

3. **Database Migration destructive** — `fallbackToDestructiveMigration()` akan menghapus semua data setiap kali schema database berubah.

4. **Tidak ada unit test** — tidak ada direktori `test/` atau `androidTest/` di project.

5. **Campuran bahasa** — UI menggunakan campuran Indonesia dan Inggris (toast Indonesia, label Inggris).

6. **Tidak ada backup/sync** — semua data tugas hanya di lokal. Jika aplikasi di-uninstall, data hilang (kecuali Firebase Auth session).

### ✅ Kelebihan

- **Arsitektur MVVM** yang rapi dengan Repository pattern
- **DiffUtil** untuk RecyclerView — performa render list optimal
- **Snackbar Undo** — user experience yang baik untuk delete
- **Multi-theme** lengkap dengan 4 pilihan warna
- **Date normalization** — mendukung 3 format tanggal berbeda
- **Database seeding** — langsung bisa dipakai setelah instal
- **Remember Me** — persistensi session login

### 🔧 Potensi Pengembangan

- Migrasi ke **ViewBinding/DataBinding** (sekarang masih pakai `findViewById`)
- Implementasi **AuthViewModel** dan **ProfileViewModel** yang sebenarnya
- Backup/sync data ke **Firebase Firestore** atau **Cloud SQL**
- Push notification untuk deadline tugas
- Dark mode
- Unit test + UI test
