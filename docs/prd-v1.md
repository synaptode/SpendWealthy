# PRD — Personal Finance + Bill Reminder + Family Budget

## 1. Ringkasan Produk

### 1.1 Nama Sementara

**SpendWealthy**

### 1.2 One-liner

Aplikasi mobile offline-first untuk membantu individu dan keluarga mengatur uang, budget bulanan, tagihan, cicilan, dokumen finansial, serta laporan keuangan sederhana dengan model subscription.

### 1.3 Product Vision

Membantu orang Indonesia memiliki kendali atas uang hariannya, memahami cashflow bulanan, menghindari lupa bayar tagihan, mengontrol cicilan/paylater, dan membangun kebiasaan finansial yang lebih sehat tanpa perlu spreadsheet rumit.

### 1.4 Product Mission

Menyediakan aplikasi personal finance yang:

* Mudah dipakai setiap hari.
* Aman untuk data finansial pribadi.
* Bisa berjalan offline.
* Cocok untuk individu, pasangan, dan keluarga.
* Memberikan insight finansial sederhana yang actionable.
* Menghasilkan recurring value sehingga layak dijual dengan subscription.

### 1.5 Elevator Pitch

Banyak orang mencatat uang secara manual di notes, Excel, atau bahkan tidak mencatat sama sekali. Akibatnya, mereka sering tidak tahu uang habis untuk apa, lupa bayar tagihan, telat membayar cicilan, dan sulit mengatur budget keluarga. SpendWealthy menyatukan pencatatan transaksi, reminder tagihan, budget keluarga, laporan cashflow, dan document vault dalam satu aplikasi mobile yang private, aman, dan mudah digunakan.

---

## 2. Latar Belakang dan Problem Statement

### 2.1 Masalah Utama

User sering mengalami:

1. Tidak tahu pengeluaran terbesar setiap bulan.
2. Lupa bayar tagihan rutin.
3. Sulit mengatur budget keluarga.
4. Cicilan dan paylater tersebar di banyak tempat.
5. Pasangan/keluarga tidak punya satu sumber data keuangan bersama.
6. Sulit mengetahui sisa uang aman sampai gajian berikutnya.
7. Dokumen penting seperti invoice, bukti pembayaran, polis, kartu garansi, dan kontrak tersebar di galeri/chat/email.
8. Excel terlalu ribet untuk kebutuhan harian.
9. Aplikasi finance yang ada terasa terlalu kompleks atau terlalu fokus investasi.

### 2.2 Opportunity

Ada peluang membangun aplikasi yang fokus pada kebutuhan finansial harian, bukan investasi tingkat lanjut:

* Budget bulanan.
* Reminder tagihan.
* Family budget.
* Cicilan/paylater tracker.
* Cashflow sederhana.
* Document vault.
* Local-first privacy.

### 2.3 Product Thesis

User akan membayar subscription jika aplikasi:

* Membantu menghindari denda/telat bayar.
* Membantu mengontrol pengeluaran.
* Memberikan rasa aman terhadap kondisi uang.
* Mempermudah diskusi finansial keluarga.
* Menyimpan data penting secara aman.
* Memberikan insight otomatis yang berulang setiap bulan.

---

## 3. Goals dan Non-Goals

### 3.1 Business Goals

1. Membangun aplikasi subscription dengan retention bulanan yang kuat.
2. Mencapai product-market fit di segmen individu muda, pasangan, keluarga muda, dan pekerja dengan cicilan/tagihan rutin.
3. Mendorong conversion dari free ke premium melalui limit yang natural, bukan mengganggu.
4. Membangun foundation KMP agar Android dan iOS dapat dikembangkan cepat dengan shared business logic.
5. Menjaga biaya operasional rendah melalui pendekatan offline-first/local-first pada fase awal.

### 3.2 User Goals

1. User tahu uang masuk dan keluar setiap bulan.
2. User tidak lupa bayar tagihan.
3. User bisa membuat dan mengikuti budget.
4. User bisa mengetahui sisa uang aman.
5. User bisa mengatur keuangan bersama pasangan/keluarga.
6. User bisa menyimpan dokumen finansial penting.
7. User bisa melihat laporan sederhana tanpa harus paham akuntansi.

### 3.3 Product Goals

1. Transaction entry harus sangat cepat.
2. Reminder tagihan harus reliable.
3. Budget status harus mudah dipahami secara visual.
4. Report harus actionable, bukan hanya chart.
5. Premium feature harus terasa bernilai.
6. App harus tetap berguna tanpa koneksi internet.
7. Data user harus aman dengan encryption dan biometric lock.

### 3.4 Non-Goals MVP

Untuk MVP, produk tidak akan mencakup:

* Integrasi langsung dengan bank.
* Open banking.
* Investasi saham/reksadana/crypto.
* Financial advice kompleks.
* Accounting bisnis lengkap.
* Pajak otomatis.
* Multi-platform web app.
* Backend sync penuh.
* Social feed.
* Marketplace produk finansial.

---

## 4. Target User dan Persona

### 4.1 Primary Persona — Pekerja Muda

**Nama:** Raka 28 tahun
**Status:** Karyawan
**Income:** Bulanan
**Pain:** Uang sering habis tanpa tahu detailnya. Banyak subscription, cicilan, dan e-wallet.
**Need:** Catatan cepat, budget, reminder tagihan, insight pengeluaran.
**Willingness to Pay:** Medium. Mau bayar jika aplikasi benar-benar membantu menghemat uang.

### 4.2 Primary Persona — Pasangan/Keluarga Muda

**Nama:** Andi dan Nisa 32 tahun
**Status:** Menikah, punya anak
**Pain:** Pengeluaran rumah tangga tidak transparan, tagihan banyak, cicilan rumah/kendaraan, biaya anak.
**Need:** Shared budget, tagihan keluarga, laporan bulanan, reminder bersama.
**Willingness to Pay:** Tinggi jika ada family sharing dan data aman.

### 4.3 Secondary Persona — Freelancer

**Nama:** Dini 30 tahun
**Status:** Freelancer
**Pain:** Income tidak tetap, sulit atur cashflow, invoice dan bukti pembayaran tercecer.
**Need:** Wallet, income tracking, recurring expense, document vault, report.
**Willingness to Pay:** Medium-high jika membantu mengatur income tidak tetap.

### 4.4 Secondary Persona — Sandwich Generation

**Nama:** Bayu 35 tahun
**Status:** Menanggung keluarga dan orang tua
**Pain:** Banyak pos pengeluaran, biaya mendadak, sulit menabung.
**Need:** Budget prioritas, emergency fund tracker, reminder, report keluarga.
**Willingness to Pay:** Medium jika value-nya jelas.

---

## 5. Design Thinking Framework

### 5.1 Empathize

Aktivitas:

* Interview 10–20 calon user.
* Observasi cara user mencatat uang saat ini.
* Pelajari spreadsheet yang mereka gunakan.
* Tanyakan pengalaman lupa bayar tagihan.
* Tanyakan masalah ketika mengatur uang bersama pasangan.

Pertanyaan interview:

1. Saat ini kamu mencatat pengeluaran di mana?
2. Apa yang paling membuat kamu merasa uang tidak terkontrol?
3. Pernah telat bayar tagihan? Kenapa?
4. Apakah kamu punya budget bulanan?
5. Bagaimana cara kamu diskusi uang dengan pasangan/keluarga?
6. Fitur apa yang membuat kamu rela bayar bulanan?
7. Apa alasan kamu berhenti memakai aplikasi finance sebelumnya?

Insight yang dicari:

* Entry transaksi harus super cepat.
* User sering lupa bukan karena tidak peduli, tapi karena friction tinggi.
* User tidak butuh chart kompleks; user butuh jawaban sederhana: “aman atau tidak?”
* Reminder tagihan bisa menjadi core retention.
* Family budget adalah pain yang kuat untuk subscription.

### 5.2 Define

Problem statement:

> Pekerja dan keluarga muda membutuhkan cara yang mudah, aman, dan konsisten untuk mencatat transaksi, mengatur budget, dan mengingat tagihan agar mereka bisa mengambil keputusan finansial harian tanpa spreadsheet rumit.

How Might We:

1. Bagaimana membuat pencatatan transaksi kurang dari 10 detik?
2. Bagaimana membuat user tidak lupa tagihan tanpa merasa terganggu?
3. Bagaimana membuat budget mudah dipahami oleh non-finance user?
4. Bagaimana membuat family budget tetap private namun kolaboratif?
5. Bagaimana membuat subscription terasa sebagai penghematan, bukan biaya tambahan?

### 5.3 Ideate

Ide solusi:

* Quick add transaction.
* Smart recurring transaction.
* Bill reminder timeline.
* Budget progress bar.
* Safe-to-spend calculation.
* Family shared wallet.
* Monthly financial health score.
* Document vault.
* OCR receipt.
* Auto insight mingguan.
* Export laporan PDF.

### 5.4 Prototype

Prototype MVP:

1. Home dashboard.
2. Add transaction bottom sheet.
3. Budget setup wizard.
4. Bill reminder setup.
5. Monthly report.
6. Family invite.
7. Premium paywall.

### 5.5 Test

Testing goals:

* Apakah user paham dashboard dalam 5 detik?
* Apakah user bisa add transaction tanpa instruksi?
* Apakah user mau membuat reminder pertama saat onboarding?
* Apakah user paham budget status?
* Apakah user merasa premium worth it?

Usability success criteria:

* 80% user bisa add transaction < 15 detik.
* 70% user bisa membuat reminder tanpa bantuan.
* 70% user paham arti “safe to spend”.
* 60% user menyelesaikan onboarding.

---

## 6. User Centered Design Principles

### 6.1 Prinsip UX

1. **Quick capture first**
   User tidak boleh merasa input transaksi itu berat.

2. **Plain language**
   Hindari istilah akuntansi rumit. Gunakan bahasa seperti “Uang masuk”, “Uang keluar”, “Sisa aman”, “Tagihan bulan ini”.

3. **Actionable insight**
   Jangan hanya tampilkan grafik. Beri kalimat seperti: “Pengeluaran makan naik 23% dari bulan lalu.”

4. **Low anxiety design**
   Finance app bisa membuat user merasa bersalah. Tone harus membantu, bukan menghakimi.

5. **Privacy by design**
   Data finansial sangat sensitif. Berikan lock, encryption, dan kontrol data.

6. **Flexible enough, not complicated**
   Fitur cukup fleksibel untuk berbagai gaya hidup, tapi default-nya harus sederhana.

7. **Family-friendly collaboration**
   Family budget harus jelas siapa menambahkan apa, tapi tidak membuat user merasa diawasi berlebihan.

### 6.2 UX Tone

Gunakan tone:

* Tenang.
* Membantu.
* Personal.
* Tidak menghakimi.
* Praktis.

Contoh microcopy:

* “Kamu masih aman untuk belanja Rp1.250.000 sampai akhir bulan.”
* “Tagihan internet jatuh tempo 3 hari lagi.”
* “Budget makan sudah hampir habis. Mau kurangi target atau cek transaksi?”
* “Tidak apa-apa, bulan ini memang banyak pengeluaran. Yuk atur ulang budget bulan depan.”

---

## 7. Product Scope

### 7.1 MVP Scope

MVP harus mencakup:

1. Onboarding.
2. Local profile setup.
3. Wallet management.
4. Transaction tracking.
5. Category management.
6. Monthly budget.
7. Bill reminder.
8. Recurring transaction.
9. Dashboard.
10. Basic report.
11. Biometric lock.
12. Local notification.
13. Subscription paywall.
14. Export CSV/PDF basic.

### 7.2 Premium Scope v1

Premium mencakup:

1. Unlimited wallet.
2. Unlimited bill reminder.
3. Unlimited budget category.
4. Family budget.
5. Shared wallet.
6. Advanced report.
7. Document vault.
8. OCR receipt.
9. Encrypted backup.
10. Custom category icon/color.
11. Export PDF advanced.
12. Monthly insight.

### 7.3 Future Scope

1. Bank statement import.
2. AI categorization.
3. AI financial coach.
4. iCloud/Google Drive sync.
5. Web dashboard.
6. Widget Android/iOS.
7. Receipt scanning automation.
8. Debt snowball/avalanche planner.
9. Goal-based saving planner.
10. Household member permission system.

---

## 8. Core Modules

### 8.1 Dashboard Module

Fungsi:

* Menampilkan ringkasan uang bulan ini.
* Menampilkan budget status.
* Menampilkan upcoming bills.
* Menampilkan insight singkat.
* Menampilkan quick action.

Komponen:

* Total income.
* Total expense.
* Balance.
* Safe to spend.
* Budget progress.
* Upcoming bills.
* Recent transactions.
* Quick add button.

### 8.2 Transaction Module

Fungsi:

* Add income.
* Add expense.
* Edit transaction.
* Delete transaction.
* Attach receipt/photo.
* Assign category.
* Assign wallet.
* Mark transaction as recurring.

Fields:

* Amount.
* Type: income/expense/transfer.
* Date.
* Category.
* Wallet.
* Note.
* Merchant.
* Attachment.
* Tags.
* Created by.

### 8.3 Wallet Module

Jenis wallet:

* Cash.
* Bank account manual.
* E-wallet.
* Credit card.
* Paylater.
* Savings.

Fungsi:

* Add wallet.
* Edit wallet.
* Archive wallet.
* Set initial balance.
* Transfer antar wallet.

### 8.4 Budget Module

Fungsi:

* Monthly budget.
* Category budget.
* Family budget.
* Alert threshold.
* Budget carryover optional.

Jenis budget:

* Personal monthly budget.
* Category budget.
* Shared family budget.
* Goal-based budget.

### 8.5 Bill Reminder Module

Fungsi:

* Add bill reminder.
* Recurring reminder.
* Due date notification.
* Mark as paid.
* Attach proof of payment.
* Auto-create transaction after paid.

Jenis tagihan:

* Internet.
* Listrik.
* Air.
* Sewa/kontrakan.
* Cicilan rumah.
* Cicilan kendaraan.
* Kartu kredit.
* Paylater.
* Asuransi.
* Sekolah.
* Subscription streaming/software.

### 8.6 Family Budget Module

Fungsi:

* Create family space.
* Invite member.
* Shared wallet.
* Shared budget.
* Shared bill.
* Member role.
* Activity log.

Role:

* Owner.
* Admin.
* Member.
* Viewer.

Permission:

* Owner: semua akses.
* Admin: edit budget, transaksi, tagihan.
* Member: add transaksi, lihat summary.
* Viewer: lihat summary terbatas.

### 8.7 Report Module

Fungsi:

* Monthly report.
* Category breakdown.
* Trend income/expense.
* Top spending.
* Bill payment history.
* Budget performance.
* Export PDF/CSV.

Insight examples:

* “Pengeluaran transport naik 18% dari bulan lalu.”
* “Kamu telat bayar 2 tagihan bulan ini.”
* “Budget makan terpakai 92% dengan 5 hari tersisa.”
* “Sisa uang aman harian kamu Rp85.000.”

### 8.8 Document Vault Module

Fungsi:

* Upload document/photo.
* Categorize document.
* Attach document to bill/transaction.
* Search by title/tag.
* Biometric protected.

Kategori:

* Bukti bayar.
* Invoice.
* Kontrak.
* Polis.
* Garansi.
* Dokumen keluarga.
* Kartu identitas.

### 8.9 Subscription Module

Fungsi:

* Free plan limits.
* Premium entitlement.
* Family plan.
* Trial period.
* Restore purchase.
* Grace period.
* Expired state.

### 8.10 Settings Module

Fungsi:

* Currency.
* Theme.
* Biometric lock.
* Notification preferences.
* Export/import data.
* Backup.
* Delete all data.
* Manage subscription.

---

## 9. Happy Flow

### 9.1 Happy Flow — New User Onboarding

1. User install app.
2. User membuka app.
3. App menampilkan value proposition:

   * Atur uang.
   * Ingat tagihan.
   * Budget keluarga.
4. User memilih mode:

   * Personal.
   * Couple/Family.
5. User memasukkan nama panggilan.
6. User memilih mata uang default: IDR.
7. User menambahkan wallet pertama:

   * Cash.
   * Bank.
   * E-wallet.
8. User memasukkan saldo awal.
9. User memilih kategori pengeluaran utama.
10. User membuat budget bulanan sederhana.
11. User membuat reminder tagihan pertama.
12. App meminta izin notification.
13. User masuk ke dashboard.
14. Dashboard menampilkan:

* Saldo.
* Budget bulan ini.
* Tagihan mendatang.
* CTA tambah transaksi.

Success state:

* User memiliki minimal 1 wallet.
* User memiliki minimal 1 budget.
* User memiliki minimal 1 bill reminder.
* User memahami dashboard utama.

### 9.2 Happy Flow — Add Expense Transaction

1. User tap tombol “+”.
2. Bottom sheet muncul.
3. User memilih “Pengeluaran”.
4. User input amount.
5. User pilih kategori.
6. User pilih wallet.
7. User optional menambahkan note/foto struk.
8. User tap Simpan.
9. App menyimpan transaksi.
10. App update saldo wallet.
11. App update budget category.
12. App menampilkan feedback:

    * “Transaksi berhasil disimpan.”
    * “Budget makan tersisa Rp450.000.”

Success state:

* Transaction tersimpan.
* Budget recalculated.
* Dashboard updated.

### 9.3 Happy Flow — Add Bill Reminder

1. User membuka tab Tagihan.
2. User tap “Tambah Tagihan”.
3. User memilih kategori tagihan.
4. User input nama tagihan.
5. User input nominal.
6. User pilih due date.
7. User pilih repeat monthly.
8. User pilih reminder:

   * 7 hari sebelum.
   * 3 hari sebelum.
   * 1 hari sebelum.
   * Hari H.
9. User tap Simpan.
10. App membuat schedule local notification.
11. App menampilkan tagihan di upcoming bills.

Success state:

* Bill tersimpan.
* Notification scheduled.
* Bill muncul di dashboard.

### 9.4 Happy Flow — Mark Bill as Paid

1. User menerima notification tagihan.
2. User tap notification.
3. App membuka detail bill.
4. User tap “Tandai Sudah Dibayar”.
5. User memilih wallet pembayaran.
6. User optional upload bukti bayar.
7. App membuat expense transaction otomatis.
8. App update bill status menjadi paid.
9. App schedule bill berikutnya jika recurring.
10. App menampilkan confirmation.

Success state:

* Bill paid.
* Expense transaction created.
* Next recurring bill generated.

### 9.5 Happy Flow — Create Family Budget

1. User membuka tab Family.
2. User tap “Buat Family Space”.
3. User memberi nama space: “Keuangan Rumah”.
4. User memilih role dirinya sebagai Owner.
5. User membuat shared budget bulanan.
6. User invite pasangan via link/kode/email.
7. Pasangan menerima invite.
8. Pasangan join sebagai Member/Admin.
9. Keduanya bisa melihat budget shared.
10. Masing-masing bisa add transaksi.
11. App menampilkan activity log.

Success state:

* Family space created.
* Member joined.
* Shared budget aktif.

### 9.6 Happy Flow — Monthly Review

1. Pada akhir bulan, app mengirim reminder “Review keuangan bulan ini”.
2. User membuka Monthly Report.
3. App menampilkan:

   * Total income.
   * Total expense.
   * Saving rate.
   * Top categories.
   * Budget performance.
   * Tagihan paid/late.
4. App memberikan insight sederhana.
5. User tap “Buat budget bulan depan”.
6. App menawarkan copy budget bulan ini.
7. User adjust budget.
8. Budget bulan depan tersimpan.

Success state:

* User memahami performa bulan ini.
* Budget bulan depan dibuat.

---

## 10. Edge Cases dan Error Flow

### 10.1 User Tidak Memberi Permission Notification

Flow:

1. User deny notification permission.
2. App tetap menyimpan bill reminder.
3. App menampilkan inline warning:

   * “Reminder tersimpan, tapi notifikasi belum aktif.”
4. App memberi CTA:

   * “Aktifkan Notifikasi”.
5. Jika user tidak aktifkan, bill tetap muncul di app.

Requirement:

* Jangan blok user.
* Jangan terus-menerus spam permission prompt.

### 10.2 User Input Amount Tidak Valid

Cases:

* Amount kosong.
* Amount 0.
* Amount negatif.
* Amount melebihi batas sistem.

Behavior:

* Show inline error.
* Disable save button sampai valid.

### 10.3 Budget Over Limit

Flow:

1. User add transaction.
2. Budget category melewati limit.
3. App tetap menyimpan transaksi.
4. App menampilkan warning:

   * “Budget makan sudah melewati batas Rp120.000.”
5. App menawarkan:

   * “Lihat transaksi kategori ini”.
   * “Ubah budget”.

### 10.4 Bill Sudah Lewat Due Date

Behavior:

* Status menjadi overdue.
* Tampilkan badge merah.
* Prioritaskan di dashboard.
* Jangan auto-create expense sampai user mark paid.

### 10.5 Recurring Bill Gagal Generate

Behavior:

* App melakukan generation saat app dibuka jika background scheduler gagal.
* Gunakan idempotency key agar tidak duplicate.

### 10.6 Family Member Offline

Behavior:

* Data lokal tetap bisa diubah.
* Saat sync tersedia, resolve conflict.
* Untuk MVP tanpa backend, family sharing bisa ditunda atau dibuat local-only simulation.

### 10.7 Subscription Expired

Behavior:

* Data premium tidak dihapus.
* Fitur premium dikunci untuk penambahan baru.
* User tetap bisa melihat data lama.
* Export tetap boleh untuk data pribadi jika memungkinkan.

### 10.8 Device Hilang

Jika backup belum aktif:

* Data lokal tidak bisa dipulihkan.
* App harus menjelaskan sejak awal bahwa backup premium diperlukan untuk restore.

Jika backup aktif:

* User login/restore backup.
* Data didekripsi di device.

---

## 11. Functional Requirements

### 11.1 Onboarding Requirements

| ID      | Requirement                                                   | Priority |
| ------- | ------------------------------------------------------------- | -------- |
| ONB-001 | User dapat memilih mode Personal atau Family                  | P0       |
| ONB-002 | User dapat membuat wallet pertama                             | P0       |
| ONB-003 | User dapat membuat budget awal                                | P0       |
| ONB-004 | User dapat membuat bill reminder pertama                      | P0       |
| ONB-005 | App meminta notification permission dengan konteks yang jelas | P0       |
| ONB-006 | User dapat skip onboarding opsional                           | P1       |
| ONB-007 | App menampilkan progress onboarding                           | P1       |

### 11.2 Transaction Requirements

| ID      | Requirement                                               | Priority |
| ------- | --------------------------------------------------------- | -------- |
| TRX-001 | User dapat menambah income                                | P0       |
| TRX-002 | User dapat menambah expense                               | P0       |
| TRX-003 | User dapat menambah transfer antar wallet                 | P0       |
| TRX-004 | User dapat edit transaction                               | P0       |
| TRX-005 | User dapat delete transaction                             | P0       |
| TRX-006 | User dapat filter by date/category/wallet                 | P1       |
| TRX-007 | User dapat attach receipt                                 | P1       |
| TRX-008 | User dapat membuat recurring transaction                  | P1       |
| TRX-009 | User dapat menambahkan tags                               | P2       |
| TRX-010 | App recalculates wallet balance after transaction changes | P0       |

### 11.3 Budget Requirements

| ID      | Requirement                                  | Priority   |
| ------- | -------------------------------------------- | ---------- |
| BGT-001 | User dapat membuat monthly budget            | P0         |
| BGT-002 | User dapat membuat category budget           | P0         |
| BGT-003 | App menghitung used/remaining budget         | P0         |
| BGT-004 | App memberi warning saat threshold tercapai  | P1         |
| BGT-005 | User dapat copy budget dari bulan sebelumnya | P1         |
| BGT-006 | User dapat reset budget bulan depan          | P1         |
| BGT-007 | User dapat membuat shared family budget      | P1 Premium |
| BGT-008 | User dapat exclude category dari budget      | P2         |
| BGT-009 | User dapat carryover unused budget           | P2         |

### 11.4 Bill Reminder Requirements

| ID       | Requirement                                 | Priority |
| -------- | ------------------------------------------- | -------- |
| BILL-001 | User dapat membuat bill reminder            | P0       |
| BILL-002 | User dapat set nominal bill                 | P0       |
| BILL-003 | User dapat set due date                     | P0       |
| BILL-004 | User dapat set recurring monthly            | P0       |
| BILL-005 | App mengirim local notification             | P0       |
| BILL-006 | User dapat mark as paid                     | P0       |
| BILL-007 | App membuat expense otomatis saat bill paid | P1       |
| BILL-008 | User dapat attach proof of payment          | P1       |
| BILL-009 | App menampilkan overdue bills               | P0       |
| BILL-010 | App generate next bill instance             | P0       |

### 11.5 Family Requirements

| ID      | Requirement                          | Priority   |
| ------- | ------------------------------------ | ---------- |
| FAM-001 | User dapat membuat family space      | P1 Premium |
| FAM-002 | User dapat invite member             | P1 Premium |
| FAM-003 | User dapat assign role               | P1 Premium |
| FAM-004 | User dapat membuat shared wallet     | P1 Premium |
| FAM-005 | User dapat membuat shared budget     | P1 Premium |
| FAM-006 | User dapat melihat activity log      | P1 Premium |
| FAM-007 | User dapat remove member             | P1 Premium |
| FAM-008 | User dapat set visibility per wallet | P2 Premium |

### 11.6 Report Requirements

| ID      | Requirement                             | Priority   |
| ------- | --------------------------------------- | ---------- |
| RPT-001 | User dapat melihat monthly summary      | P0         |
| RPT-002 | User dapat melihat spending by category | P0         |
| RPT-003 | User dapat melihat income vs expense    | P0         |
| RPT-004 | User dapat export CSV                   | P1         |
| RPT-005 | User dapat export PDF                   | P1 Premium |
| RPT-006 | App menampilkan monthly insight         | P1 Premium |
| RPT-007 | App menampilkan trend 3/6/12 bulan      | P1 Premium |

### 11.7 Document Vault Requirements

| ID      | Requirement                                     | Priority   |
| ------- | ----------------------------------------------- | ---------- |
| DOC-001 | User dapat upload document/image                | P1 Premium |
| DOC-002 | User dapat attach document ke transaction       | P1 Premium |
| DOC-003 | User dapat attach document ke bill              | P1 Premium |
| DOC-004 | User dapat search document                      | P1 Premium |
| DOC-005 | User dapat lock document vault dengan biometric | P1 Premium |
| DOC-006 | User dapat OCR document                         | P2 Premium |

### 11.8 Subscription Requirements

| ID      | Requirement                                        | Priority |
| ------- | -------------------------------------------------- | -------- |
| SUB-001 | App memiliki free plan                             | P0       |
| SUB-002 | App memiliki monthly premium                       | P0       |
| SUB-003 | App memiliki annual premium                        | P0       |
| SUB-004 | App support restore purchase                       | P0       |
| SUB-005 | App menampilkan paywall saat limit tercapai        | P0       |
| SUB-006 | App tidak menghapus data saat subscription expired | P0       |
| SUB-007 | App support family plan                            | P1       |

---

## 12. Non-Functional Requirements

### 12.1 Performance

* App cold start < 3 detik di mid-range device.
* Add transaction save < 500 ms lokal.
* Dashboard calculation < 1 detik untuk 10.000 transaksi.
* Search transaction < 1 detik untuk 10.000 transaksi.
* Report generation < 3 detik untuk 12 bulan data.

### 12.2 Reliability

* Local database transaction harus atomic.
* Reminder generation harus idempotent.
* Notification schedule harus resilient terhadap app restart.
* Backup restore harus memiliki checksum/integrity validation.

### 12.3 Security

* Sensitive data encrypted at rest.
* Biometric/PIN lock optional.
* Backup encrypted sebelum upload.
* Jangan log amount, notes, document path di production logs.
* Jangan simpan secret hardcoded di shared code.
* Gunakan secure storage untuk keys.

### 12.4 Privacy

* App harus menjelaskan data apa yang disimpan.
* Untuk MVP local-first, data utama disimpan di device.
* User dapat export data.
* User dapat delete all data.
* Tidak menjual data user.
* Analytics harus minim dan tidak menyertakan data finansial mentah.

### 12.5 Accessibility

* Support dynamic font size.
* Contrast cukup untuk chart dan status.
* Jangan hanya mengandalkan warna; gunakan label dan icon.
* Touch target minimal 44x44 pt.
* Screen reader friendly untuk form utama.

### 12.6 Localization

* MVP: Bahasa Indonesia.
* Currency: IDR default.
* Future: English.
* Date format: dd MMM yyyy.
* Amount format: Rp1.250.000.

---

## 13. Information Architecture

### 13.1 Bottom Navigation

1. **Home**
2. **Transaksi**
3. **Tagihan**
4. **Budget**
5. **Laporan**

Alternative jika ingin simple:

1. Home
2. Transaksi
3. Budget
4. Tagihan
5. Saya

### 13.2 Home Screen Sections

* Header greeting.
* Current month selector.
* Balance summary card.
* Safe to spend card.
* Budget progress card.
* Upcoming bills card.
* Recent transactions.
* Quick actions.

### 13.3 Transaction Screen

* Search bar.
* Filter chips.
* Monthly grouped list.
* Transaction item.
* Floating add button.

### 13.4 Bill Screen

* Upcoming tab.
* Paid tab.
* Overdue tab.
* Calendar/list view.
* Add bill CTA.

### 13.5 Budget Screen

* Monthly budget summary.
* Category progress.
* Edit budget.
* Copy previous month.
* Family budget toggle.

### 13.6 Report Screen

* Month selector.
* Income vs expense.
* Category breakdown.
* Top spending.
* Bills history.
* Export CTA.

### 13.7 Settings/Profile

* Account/local profile.
* Wallets.
* Categories.
* Family space.
* Subscription.
* Security.
* Backup/export.
* Notification.
* Help.

---

## 14. UX Flow Detail

### 14.1 Quick Add Transaction UX

Recommended UI:

* Bottom sheet.
* Numeric keypad.
* Type selector: Expense / Income / Transfer.
* Category chips.
* Wallet selector.
* Date default today.
* Save button sticky bottom.

Required behavior:

* Default type: Expense.
* Default wallet: last used wallet.
* Default date: today.
* Suggested category based on last used.
* Save button disabled until amount valid.

### 14.2 Bill Reminder UX

Recommended UI:

* Bill template shortcuts:

  * Internet.
  * Listrik.
  * Cicilan.
  * Paylater.
  * Kartu kredit.
  * Lainnya.
* Due date calendar picker.
* Repeat selector.
* Reminder timing selector.
* Preview notification copy.

### 14.3 Budget Setup UX

Recommended UI:

* Ask monthly income optional.
* Suggest common categories.
* Let user set total monthly spending target.
* Distribute budget manually or auto-suggest.

Example categories:

* Makan.
* Transport.
* Belanja bulanan.
* Cicilan.
* Tagihan.
* Hiburan.
* Kesehatan.
* Anak.
* Tabungan.
* Lainnya.

### 14.4 Family Budget UX

Recommended UI:

* Family dashboard separate from personal.
* Clear privacy label:

  * Personal wallet.
  * Shared wallet.
  * Shared budget.
* Activity feed:

  * “Nisa menambahkan pengeluaran Belanja Bulanan Rp350.000.”
* Permission explanation before invite.

---

## 15. Monetization Strategy

### 15.1 Free Plan

Free plan harus cukup berguna agar user percaya, tapi punya limit natural.

Free:

* 1 user.
* 2 wallets.
* 30 transactions/month.
* 3 bill reminders.
* 5 categories budget.
* Basic monthly summary.
* Local data only.

### 15.2 Premium Personal

Premium Personal:

* Unlimited transactions.
* Unlimited wallets.
* Unlimited reminders.
* Unlimited categories.
* Advanced report.
* Export PDF/CSV.
* Document vault.
* Biometric lock.
* OCR limited/month.
* Encrypted backup.
* Custom categories.

Suggested pricing:

* Rp29.000/month.
* Rp249.000/year.

### 15.3 Premium Family

Premium Family:

* Semua fitur Premium Personal.
* Family space.
* Up to 4 members.
* Shared budget.
* Shared bills.
* Activity log.
* Role permission.
* Shared document vault.

Suggested pricing:

* Rp59.000/month.
* Rp499.000/year.

### 15.4 Paywall Trigger

Paywall muncul saat:

* User mencapai limit wallet.
* User mencapai limit reminders.
* User ingin export PDF.
* User ingin family space.
* User ingin backup.
* User ingin OCR.
* User membuka advanced report.

### 15.5 Trial Strategy

Recommended:

* 7-day trial untuk Premium Personal.
* 14-day trial untuk Family Plan.
* Annual plan highlight dengan discount.

### 15.6 Subscription Value Messaging

Jangan jual “fitur”, jual outcome:

* “Hindari telat bayar tagihan.”
* “Lihat uang keluarga dalam satu dashboard.”
* “Backup data finansial penting.”
* “Export laporan bulanan.”
* “Simpan bukti bayar dengan aman.”

---

## 16. High Level Design — System Architecture

### 16.1 Architecture Style

Recommended architecture:

* Kotlin Multiplatform shared domain + data + use cases.
* Offline-first/local-first.
* Clean Architecture.
* MVVM/MVI presentation.
* Repository pattern.
* Platform-specific services for notification, biometric, billing, file system, OCR.

### 16.2 High Level Component Diagram

```txt
+------------------------------------------------------+
|                    Android App                        |
|  Compose UI / Android UI                              |
|  Notification Adapter                                 |
|  Play Billing Adapter                                 |
|  Biometric Adapter                                    |
|  File/OCR Adapter                                     |
+----------------------------+-------------------------+
                             |
                             | shared API
                             |
+----------------------------v-------------------------+
|                    KMP Shared Module                  |
|                                                      |
|  presentation/                                        |
|    ViewModel / State / Event                          |
|                                                      |
|  domain/                                              |
|    Entity                                             |
|    Value Object                                       |
|    Use Case                                           |
|    Domain Service                                     |
|                                                      |
|  data/                                                |
|    Repository Impl                                    |
|    Local Data Source                                  |
|    Backup Data Source                                 |
|                                                      |
|  core/                                                |
|    Date/Time                                          |
|    Money                                              |
|    Result/Error                                       |
|    Validation                                         |
+----------------------------^-------------------------+
                             |
                             | shared API
                             |
+----------------------------+-------------------------+
|                      iOS App                          |
|  SwiftUI / Compose Multiplatform UI                   |
|  UNNotification Adapter                               |
|  StoreKit Adapter                                     |
|  Face ID/Touch ID Adapter                             |
|  File/OCR Adapter                                     |
+------------------------------------------------------+
```

### 16.3 Layering

```txt
UI Layer
  Screen
  Component
  ViewModel
  UiState
  UiEvent

Domain Layer
  UseCase
  Entity
  ValueObject
  Repository Interface
  Domain Service

Data Layer
  Repository Implementation
  Local Database
  Preferences
  Encryption
  Backup

Platform Layer
  Notification
  Billing
  Biometric
  File Storage
  OCR
  Background Task
```

### 16.4 Offline-first Strategy

MVP:

* Semua transaksi disimpan lokal.
* Report dihitung lokal.
* Reminder dijadwalkan lokal.
* Subscription entitlement dapat dicache lokal.

Future:

* Encrypted cloud backup.
* Sync antar device.
* Family collaboration real-time/near-real-time.

### 16.5 Local Database Recommendation

Options:

1. **SQLDelight**

   * Cocok untuk KMP.
   * Strong typing.
   * SQL explicit.
2. **Realm Kotlin**

   * Object database.
   * KMP support.
3. **Room KMP** jika sudah sesuai kebutuhan project.

Recommendation:

* Gunakan SQLDelight untuk domain finansial karena query report dan aggregate lebih eksplisit.

### 16.6 Dependency Direction

Rule:

* UI depends on Domain.
* Data depends on Domain.
* Domain tidak boleh tahu UI/platform.
* Platform-specific adapter mengimplementasikan interface dari shared module.

---

## 17. Suggested KMP Module Structure

```txt
spendwealthy/
  shared/
    build.gradle.kts
    src/
      commonMain/
        kotlin/
          core/
            money/
            datetime/
            result/
            validation/
          domain/
            transaction/
            wallet/
            budget/
            bill/
            family/
            report/
            document/
            subscription/
          data/
            local/
            repository/
            mapper/
          presentation/
            home/
            transaction/
            budget/
            bill/
            report/
            settings/
          platform/
            NotificationScheduler.kt
            BillingGateway.kt
            BiometricGateway.kt
            FileGateway.kt
            OcrGateway.kt
      androidMain/
        kotlin/
          platform/
      iosMain/
        kotlin/
          platform/

  androidApp/
    src/main/java/...

  iosApp/
    iosApp/...
```

### 17.1 Shared Interfaces

```kotlin
interface NotificationScheduler {
    suspend fun scheduleBillReminder(reminder: BillReminderSchedule): Result<Unit>
    suspend fun cancelReminder(reminderId: String): Result<Unit>
}

interface BillingGateway {
    suspend fun getEntitlement(): SubscriptionEntitlement
    suspend fun purchase(planId: String): PurchaseResult
    suspend fun restore(): RestoreResult
}

interface BiometricGateway {
    suspend fun isAvailable(): Boolean
    suspend fun authenticate(reason: String): Boolean
}

interface SecureStorage {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun remove(key: String)
}

interface FileGateway {
    suspend fun saveDocument(input: DocumentInput): StoredDocument
    suspend fun deleteDocument(documentId: String)
}
```

---

## 18. Domain Model

### 18.1 Entity Overview

Core entities:

* UserProfile
* Wallet
* Transaction
* Category
* Budget
* BudgetPeriod
* BillReminder
* BillInstance
* FamilySpace
* FamilyMember
* Document
* SubscriptionEntitlement
* ReportSnapshot

### 18.2 UserProfile

```kotlin
data class UserProfile(
    val id: String,
    val name: String,
    val currency: CurrencyCode,
    val mode: UserMode,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class UserMode {
    PERSONAL,
    FAMILY
}
```

### 18.3 Money Value Object

```kotlin
data class Money(
    val amountMinor: Long,
    val currency: CurrencyCode
) {
    init {
        require(amountMinor >= 0)
    }
}

enum class CurrencyCode {
    IDR
}
```

Use minor unit internally:

* Rp10.000 stored as 10000 if IDR without decimal.
* For future multi-currency, abstract via currency metadata.

### 18.4 Wallet

```kotlin
data class Wallet(
    val id: String,
    val name: String,
    val type: WalletType,
    val initialBalance: Money,
    val currentBalance: Money,
    val isArchived: Boolean,
    val ownerType: OwnerType,
    val familySpaceId: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class WalletType {
    CASH,
    BANK,
    EWALLET,
    CREDIT_CARD,
    PAYLATER,
    SAVINGS,
    OTHER
}

enum class OwnerType {
    PERSONAL,
    FAMILY_SHARED
}
```

### 18.5 Transaction

```kotlin
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Money,
    val walletId: String,
    val destinationWalletId: String?,
    val categoryId: String?,
    val date: LocalDate,
    val note: String?,
    val merchant: String?,
    val tags: List<String>,
    val attachmentIds: List<String>,
    val recurringRuleId: String?,
    val billInstanceId: String?,
    val createdBy: String,
    val familySpaceId: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?
)

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}
```

### 18.6 Category

```kotlin
data class Category(
    val id: String,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val color: String,
    val isDefault: Boolean,
    val isArchived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

### 18.7 Budget

```kotlin
data class Budget(
    val id: String,
    val name: String,
    val period: BudgetPeriod,
    val totalLimit: Money?,
    val categoryLimits: List<CategoryBudgetLimit>,
    val ownerType: OwnerType,
    val familySpaceId: String?,
    val alertThresholdPercent: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class BudgetPeriod(
    val month: Int,
    val year: Int
)

data class CategoryBudgetLimit(
    val categoryId: String,
    val limit: Money
)
```

### 18.8 BillReminder

```kotlin
data class BillReminder(
    val id: String,
    val name: String,
    val categoryId: String?,
    val expectedAmount: Money,
    val dueDay: Int,
    val recurrence: RecurrenceRule,
    val reminderOffsets: List<Int>,
    val defaultWalletId: String?,
    val ownerType: OwnerType,
    val familySpaceId: String?,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class RecurrenceRule(
    val frequency: RecurrenceFrequency,
    val interval: Int
)

enum class RecurrenceFrequency {
    ONCE,
    WEEKLY,
    MONTHLY,
    YEARLY
}
```

### 18.9 BillInstance

```kotlin
data class BillInstance(
    val id: String,
    val billReminderId: String,
    val dueDate: LocalDate,
    val amount: Money,
    val status: BillStatus,
    val paidAt: Instant?,
    val paidTransactionId: String?,
    val proofDocumentId: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class BillStatus {
    UPCOMING,
    DUE_SOON,
    OVERDUE,
    PAID,
    SKIPPED
}
```

### 18.10 FamilySpace

```kotlin
data class FamilySpace(
    val id: String,
    val name: String,
    val ownerUserId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class FamilyMember(
    val id: String,
    val familySpaceId: String,
    val displayName: String,
    val role: FamilyRole,
    val status: MemberStatus,
    val joinedAt: Instant?
)

enum class FamilyRole {
    OWNER,
    ADMIN,
    MEMBER,
    VIEWER
}

enum class MemberStatus {
    INVITED,
    ACTIVE,
    REMOVED
}
```

### 18.11 Document

```kotlin
data class Document(
    val id: String,
    val title: String,
    val type: DocumentType,
    val localPath: String,
    val mimeType: String,
    val sizeBytes: Long,
    val linkedTransactionId: String?,
    val linkedBillInstanceId: String?,
    val tags: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class DocumentType {
    RECEIPT,
    INVOICE,
    PAYMENT_PROOF,
    CONTRACT,
    WARRANTY,
    IDENTITY,
    OTHER
}
```

---

## 19. Database High Level Schema

```sql
CREATE TABLE wallets (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  type TEXT NOT NULL,
  initial_balance INTEGER NOT NULL,
  current_balance INTEGER NOT NULL,
  currency TEXT NOT NULL,
  owner_type TEXT NOT NULL,
  family_space_id TEXT,
  is_archived INTEGER NOT NULL DEFAULT 0,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);

CREATE TABLE categories (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  type TEXT NOT NULL,
  icon TEXT NOT NULL,
  color TEXT NOT NULL,
  is_default INTEGER NOT NULL,
  is_archived INTEGER NOT NULL DEFAULT 0,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);

CREATE TABLE transactions (
  id TEXT PRIMARY KEY,
  type TEXT NOT NULL,
  amount INTEGER NOT NULL,
  currency TEXT NOT NULL,
  wallet_id TEXT NOT NULL,
  destination_wallet_id TEXT,
  category_id TEXT,
  date TEXT NOT NULL,
  note TEXT,
  merchant TEXT,
  recurring_rule_id TEXT,
  bill_instance_id TEXT,
  created_by TEXT NOT NULL,
  family_space_id TEXT,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL,
  deleted_at TEXT
);

CREATE TABLE budgets (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  month INTEGER NOT NULL,
  year INTEGER NOT NULL,
  total_limit INTEGER,
  currency TEXT NOT NULL,
  owner_type TEXT NOT NULL,
  family_space_id TEXT,
  alert_threshold_percent INTEGER NOT NULL,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);

CREATE TABLE category_budget_limits (
  id TEXT PRIMARY KEY,
  budget_id TEXT NOT NULL,
  category_id TEXT NOT NULL,
  limit_amount INTEGER NOT NULL,
  currency TEXT NOT NULL
);

CREATE TABLE bill_reminders (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  category_id TEXT,
  expected_amount INTEGER NOT NULL,
  currency TEXT NOT NULL,
  due_day INTEGER NOT NULL,
  frequency TEXT NOT NULL,
  interval_value INTEGER NOT NULL,
  default_wallet_id TEXT,
  owner_type TEXT NOT NULL,
  family_space_id TEXT,
  is_active INTEGER NOT NULL,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);

CREATE TABLE bill_instances (
  id TEXT PRIMARY KEY,
  bill_reminder_id TEXT NOT NULL,
  due_date TEXT NOT NULL,
  amount INTEGER NOT NULL,
  currency TEXT NOT NULL,
  status TEXT NOT NULL,
  paid_at TEXT,
  paid_transaction_id TEXT,
  proof_document_id TEXT,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);

CREATE TABLE documents (
  id TEXT PRIMARY KEY,
  title TEXT NOT NULL,
  type TEXT NOT NULL,
  local_path TEXT NOT NULL,
  mime_type TEXT NOT NULL,
  size_bytes INTEGER NOT NULL,
  linked_transaction_id TEXT,
  linked_bill_instance_id TEXT,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);
```

Important indexes:

```sql
CREATE INDEX idx_transactions_date ON transactions(date);
CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_category_id ON transactions(category_id);
CREATE INDEX idx_bill_instances_due_date ON bill_instances(due_date);
CREATE INDEX idx_bill_instances_status ON bill_instances(status);
CREATE INDEX idx_budgets_month_year ON budgets(month, year);
```

---

## 20. Use Case Design

### 20.1 Add Transaction Use Case

Responsibilities:

* Validate amount.
* Validate wallet exists.
* Validate category type matches transaction type.
* Insert transaction.
* Update wallet balance.
* Recalculate budget status.
* Emit domain event.

Pseudo flow:

```txt
Input AddTransactionCommand
Validate command
Load wallet
Load category if exists
Begin DB transaction
Insert transaction
Update wallet balance
Update budget snapshot if needed
Commit
Return transaction
```

### 20.2 Mark Bill As Paid Use Case

Responsibilities:

* Validate bill instance exists.
* Validate not already paid.
* Create expense transaction.
* Update bill instance status.
* Attach proof if exists.
* Generate next bill instance if recurring.
* Schedule next reminder.

Pseudo flow:

```txt
Input MarkBillAsPaidCommand
Load bill instance
Load bill reminder
Begin DB transaction
Create expense transaction
Set bill status PAID
Generate next instance if recurring
Commit
Schedule next reminder
Return paid bill instance
```

### 20.3 Calculate Safe To Spend Use Case

Definition:
Safe to spend adalah estimasi uang yang bisa dipakai sampai akhir periode setelah memperhitungkan tagihan mendatang dan budget minimum.

Simplified formula:

```txt
safeToSpend = availableBalance - upcomingBills - reservedBudgetRemaining - savingGoalContribution
```

MVP formula:

```txt
safeToSpend = totalWalletBalance - unpaidBillsUntilEndOfMonth
```

Advanced formula:

```txt
safeToSpend = totalLiquidBalance
              - upcomingBillsUntilNextIncome
              - requiredBudgetRemaining
              - activeSavingGoalRequiredAmount
```

### 20.4 Generate Monthly Report Use Case

Responsibilities:

* Aggregate income.
* Aggregate expense.
* Group expense by category.
* Calculate budget usage.
* Calculate bill payment performance.
* Generate insight text.

---

## 21. Business Rules

### 21.1 Wallet Balance Rules

* Income increases wallet balance.
* Expense decreases wallet balance.
* Transfer decreases source wallet and increases destination wallet.
* Deleting transaction reverses balance effect.
* Editing transaction recalculates delta.

### 21.2 Budget Rules

* Only expense transactions count toward budget.
* Transfer does not count toward budget.
* Deleted transactions do not count.
* Archived categories can still appear in historical reports.
* Budget period uses user local timezone.

### 21.3 Bill Rules

* Bill instance can be Upcoming, Due Soon, Overdue, Paid, or Skipped.
* Recurring bill must generate next instance once current instance is paid/skipped or when entering next period.
* Duplicate bill instance must be prevented via unique key:

  * billReminderId + dueDate.

### 21.4 Subscription Rules

* Free limit prevents creating new premium-limited resources.
* Existing data remains accessible after downgrade.
* Family space becomes read-only after family subscription expires.
* User can export/delete data even after subscription expires.

### 21.5 Document Rules

* Document linked to deleted transaction should not be auto-deleted unless user confirms.
* Document vault can be locked separately.
* OCR output should be editable.

---

## 22. State Management Design

### 22.1 UI State Example — Home

```kotlin
data class HomeUiState(
    val isLoading: Boolean = true,
    val selectedMonth: YearMonth,
    val totalBalance: Money? = null,
    val incomeThisMonth: Money? = null,
    val expenseThisMonth: Money? = null,
    val safeToSpend: Money? = null,
    val budgetSummaries: List<BudgetSummaryUi> = emptyList(),
    val upcomingBills: List<BillUi> = emptyList(),
    val recentTransactions: List<TransactionUi> = emptyList(),
    val insight: String? = null,
    val errorMessage: String? = null
)
```

### 22.2 UI Event Example

```kotlin
sealed interface HomeEvent {
    data object OnAddTransactionClicked : HomeEvent
    data object OnAddBillClicked : HomeEvent
    data class OnMonthChanged(val month: YearMonth) : HomeEvent
    data class OnBillClicked(val billId: String) : HomeEvent
    data class OnTransactionClicked(val transactionId: String) : HomeEvent
}
```

### 22.3 UI Effect Example

```kotlin
sealed interface HomeEffect {
    data object NavigateToAddTransaction : HomeEffect
    data class ShowToast(val message: String) : HomeEffect
    data class NavigateToBillDetail(val billId: String) : HomeEffect
}
```

---

## 23. API / Backend Strategy

### 23.1 MVP Without Backend

MVP bisa berjalan tanpa backend:

* Data lokal.
* Local notification.
* StoreKit/Play Billing langsung.
* Export/import file.
* Manual backup file encrypted.

Pros:

* Biaya rendah.
* Cepat launch.
* Privacy kuat.
* Complexity rendah.

Cons:

* Family collaboration terbatas.
* Cross-device sync tidak seamless.
* Restore device hilang sulit tanpa backup.
* Subscription entitlement validation lebih terbatas.

### 23.2 Phase 2 Minimal Backend

Backend minimal untuk:

* Account.
* Encrypted backup metadata.
* Subscription receipt validation.
* Family invite.
* Sync metadata.

Important:

* Data finansial bisa tetap end-to-end encrypted.
* Server tidak perlu bisa membaca amount/note transaksi.

### 23.3 Future Sync Architecture

```txt
Mobile App
  Local DB
  Sync Engine
  Encryption Layer
      |
      v
Backend API
  Auth
  Subscription validation
  Encrypted blob storage
  Family membership metadata
      |
      v
Storage
  Encrypted user backup
  Sync change log
```

---

## 24. Security and Privacy Architecture

### 24.1 Threat Model

Risiko:

1. Device hilang.
2. App dibuka orang lain.
3. Backup bocor.
4. Log mengandung data finansial.
5. Attachment dokumen terbaca app lain.
6. Family member melihat data yang tidak seharusnya.
7. Subscription bypass.

### 24.2 Security Controls

MVP:

* Biometric lock.
* Local database encryption jika library mendukung.
* Secure storage untuk encryption key.
* File vault private app directory.
* No sensitive logs.
* App lock timeout.

Premium/Future:

* Encrypted backup.
* End-to-end encrypted sync.
* Role-based access control for family.
* Audit log.
* Device revoke.

### 24.3 Data Classification

Highly sensitive:

* Amount.
* Transaction notes.
* Wallet names.
* Documents.
* Bill names.

Sensitive:

* Category names.
* Budget limits.
* Family member names.

Non-sensitive:

* App version.
* Device type.
* Feature usage event without financial values.

### 24.4 Analytics Privacy Rule

Do not send:

* Transaction amount.
* Category name custom.
* Wallet name.
* Note.
* Document title.

Allowed analytics:

* transaction_created count.
* bill_reminder_created count.
* budget_created count.
* paywall_viewed.
* subscription_started.
* export_used.

---

## 25. Analytics and Metrics

### 25.1 North Star Metric

**Monthly Active Budgeted Users**

Definition:
Number of users who, in a month:

* Have at least 1 active budget.
* Add at least 5 transactions.
* Have at least 1 bill reminder or view report.

### 25.2 Activation Metrics

* Onboarding completion rate.
* Wallet created rate.
* First transaction created rate.
* First bill reminder created rate.
* First budget created rate.
* Time to first transaction.

### 25.3 Engagement Metrics

* DAU/WAU/MAU.
* Transactions per active user per week.
* Bill reminders created per user.
* Budget view frequency.
* Report view frequency.
* Family invite sent.
* Family member joined.

### 25.4 Retention Metrics

* D1 retention.
* D7 retention.
* D30 retention.
* Month 2 retention.
* Reminder notification open rate.
* Monthly review completion rate.

### 25.5 Monetization Metrics

* Paywall view to trial start.
* Trial to paid conversion.
* Free to paid conversion.
* Monthly churn.
* Annual plan adoption.
* ARPU.
* LTV.

### 25.6 Quality Metrics

* Crash-free users.
* Notification delivery success proxy.
* Database error rate.
* Report generation failure.
* Purchase failure rate.
* Restore purchase success rate.

---

## 26. Event Tracking Plan

### 26.1 Event Naming Convention

Use snake_case:

* onboarding_started
* wallet_created
* transaction_created
* bill_reminder_created
* budget_created
* report_viewed
* paywall_viewed
* subscription_started
* subscription_restored

### 26.2 Event Examples

```json
{
  "event": "transaction_created",
  "properties": {
    "transaction_type": "expense",
    "has_category": true,
    "has_note": false,
    "has_attachment": false,
    "source": "quick_add"
  }
}
```

Do not include amount.

```json
{
  "event": "paywall_viewed",
  "properties": {
    "trigger": "bill_limit",
    "plan_context": "premium_personal"
  }
}
```

```json
{
  "event": "bill_reminder_created",
  "properties": {
    "recurrence": "monthly",
    "reminder_offsets_count": 3
  }
}
```

---

## 27. Design System Direction

### 27.1 Visual Personality

* Calm.
* Trustworthy.
* Clean.
* Friendly.
* Not too playful.

### 27.2 Color Direction

Suggested:

* Primary: green/teal for finance-positive feeling.
* Warning: amber.
* Danger: red.
* Neutral: gray/blue-gray.

### 27.3 Component Library

Core components:

* MoneySummaryCard.
* BudgetProgressCard.
* BillReminderCard.
* TransactionListItem.
* WalletPicker.
* CategoryChip.
* MonthSelector.
* EmptyState.
* PaywallSheet.
* NumericAmountInput.
* SecureVaultCard.

### 27.4 Empty State Examples

No transaction:

> “Belum ada transaksi bulan ini. Tambahkan transaksi pertama agar kamu bisa lihat cashflow.”

No bill:

> “Belum ada tagihan. Tambahkan tagihan rutin supaya tidak lupa bayar.”

No budget:

> “Budget membantu kamu tahu batas aman pengeluaran bulan ini.”

### 27.5 Status Labels

Budget:

* Aman.
* Hampir habis.
* Melewati budget.

Bill:

* Akan datang.
* Jatuh tempo segera.
* Terlambat.
* Sudah dibayar.

---

## 28. Screen Requirements

### 28.1 Home Screen

Must show:

* Current month.
* Total balance.
* Income/expense summary.
* Safe to spend.
* Upcoming bills.
* Budget status.
* Recent transactions.

CTA:

* Add transaction.
* Add bill.
* View report.

### 28.2 Add Transaction Screen/Sheet

Must support:

* Expense/income/transfer.
* Amount input.
* Category.
* Wallet.
* Date.
* Note.
* Save.

### 28.3 Transaction Detail

Must support:

* View detail.
* Edit.
* Delete.
* View linked bill.
* View attachment.

### 28.4 Budget Screen

Must support:

* View budget list.
* Create/edit budget.
* Category progress.
* Budget warning.

### 28.5 Bill Screen

Must support:

* Upcoming bills.
* Overdue bills.
* Paid bills.
* Add/edit bill.
* Mark as paid.

### 28.6 Report Screen

Must support:

* Monthly summary.
* Category chart.
* Top spending list.
* Budget performance.
* Export.

### 28.7 Family Screen

Must support:

* Create family space.
* Invite member.
* Manage role.
* Shared budget.
* Shared bills.
* Activity log.

### 28.8 Subscription Screen

Must support:

* Plan comparison.
* Monthly/annual toggle.
* Trial messaging.
* Restore purchase.
* Terms link.
* Privacy link.

---

## 29. Notification Strategy

### 29.1 Notification Types

1. Bill due soon.
2. Bill due today.
3. Bill overdue.
4. Budget threshold warning.
5. Monthly review.
6. Backup reminder.

### 29.2 Notification Copy

Bill due soon:

> “Tagihan Internet jatuh tempo 3 hari lagi.”

Bill due today:

> “Tagihan Kartu Kredit jatuh tempo hari ini.”

Overdue:

> “Tagihan Paylater sudah lewat jatuh tempo.”

Budget warning:

> “Budget makan sudah terpakai 85%.”

Monthly review:

> “Laporan bulan ini sudah siap. Yuk cek cashflow kamu.”

### 29.3 Notification Rules

* User bisa memilih reminder offsets.
* Jangan kirim budget warning terlalu sering.
* Jangan kirim notification malam hari kecuali user pilih.
* Default quiet hours: 21:00–07:00.
* Notification harus deep link ke screen terkait.

---

## 30. Subscription Entitlement Logic

### 30.1 Entitlement Model

```kotlin
data class SubscriptionEntitlement(
    val plan: PlanType,
    val status: SubscriptionStatus,
    val expiresAt: Instant?,
    val features: Set<PremiumFeature>
)

enum class PlanType {
    FREE,
    PREMIUM_PERSONAL,
    PREMIUM_FAMILY
}

enum class SubscriptionStatus {
    ACTIVE,
    TRIAL,
    GRACE_PERIOD,
    EXPIRED,
    UNKNOWN
}

enum class PremiumFeature {
    UNLIMITED_WALLET,
    UNLIMITED_TRANSACTION,
    UNLIMITED_BILL,
    ADVANCED_REPORT,
    PDF_EXPORT,
    DOCUMENT_VAULT,
    OCR,
    FAMILY_SPACE,
    ENCRYPTED_BACKUP
}
```

### 30.2 Limit Checker

```kotlin
interface EntitlementChecker {
    fun canCreateWallet(currentCount: Int): EntitlementDecision
    fun canCreateBillReminder(currentCount: Int): EntitlementDecision
    fun canUseFamilySpace(): EntitlementDecision
    fun canExportPdf(): EntitlementDecision
    fun canUseDocumentVault(): EntitlementDecision
}

sealed interface EntitlementDecision {
    data object Allowed : EntitlementDecision
    data class Denied(val reason: DenialReason, val paywallTrigger: String) : EntitlementDecision
}
```

---

## 31. Testing Strategy

### 31.1 Unit Tests

Coverage:

* Money calculation.
* Wallet balance update.
* Budget calculation.
* Bill recurrence generation.
* Safe to spend calculation.
* Subscription limit checker.
* Date/time edge cases.

### 31.2 Integration Tests

Coverage:

* Add transaction updates wallet and budget.
* Mark bill paid creates transaction.
* Delete transaction reverses wallet balance.
* Report aggregates correctly.
* Backup export/import integrity.

### 31.3 UI Tests

Critical flows:

* Onboarding.
* Add transaction.
* Add bill.
* Mark bill paid.
* Create budget.
* View report.
* Purchase premium.
* Restore purchase.

### 31.4 Edge Date Tests

Must test:

* End of month.
* February 28/29.
* Due day 31 in shorter months.
* Timezone changes.
* Daylight saving if app expands globally.

### 31.5 Security Tests

* App lock behavior.
* Sensitive logs check.
* Document access path.
* Backup encryption/decryption.
* Restore wrong key failure.

---

## 32. Release Plan

### 32.1 Phase 0 — Discovery and Validation

Duration target: 2–4 weeks.

Output:

* User interviews.
* Competitor review.
* Pricing hypothesis.
* Clickable prototype.
* MVP scope locked.

Validation:

* 20 target users.
* 5 willingness-to-pay interviews.
* 1 landing page waitlist.

### 32.2 Phase 1 — MVP Beta

Scope:

* Onboarding.
* Wallet.
* Transaction.
* Category.
* Budget.
* Bill reminder.
* Basic report.
* Local notification.
* Biometric lock.

Success criteria:

* 100 beta users.
* 50% create first transaction.
* 30% create bill reminder.
* D7 retention > 20%.

### 32.3 Phase 2 — Subscription Launch

Scope:

* Premium entitlement.
* StoreKit.
* Play Billing.
* Paywall.
* Export PDF/CSV.
* Advanced report.
* Document vault.

Success criteria:

* Free to paid conversion > 2% early target.
* Trial to paid > 20% early target.
* Crash-free users > 99%.

### 32.4 Phase 3 — Family Budget

Scope:

* Family space.
* Invite member.
* Shared budget.
* Shared bills.
* Activity log.
* Family plan.

Success criteria:

* Family invite sent by 10% premium users.
* Family plan conversion validates pricing.

### 32.5 Phase 4 — Backup and Intelligence

Scope:

* Encrypted backup.
* OCR receipt.
* AI categorization.
* Monthly insight.
* Safe-to-spend advanced.

---

## 33. MVP Prioritization

### 33.1 P0 — Must Have

* Onboarding.
* Wallet.
* Add/edit/delete transaction.
* Category.
* Monthly budget.
* Bill reminder.
* Local notification.
* Dashboard.
* Basic report.
* App lock.

### 33.2 P1 — Should Have

* Recurring transaction.
* Mark bill as paid creates transaction.
* Export CSV.
* Export PDF.
* Paywall.
* Subscription.
* Document vault.
* Advanced report.

### 33.3 P2 — Could Have

* OCR receipt.
* Family budget.
* Encrypted backup.
* Widget.
* AI insight.
* Multi-currency.

### 33.4 Not Now

* Bank integration.
* Real-time sync.
* Investment portfolio.
* Tax calculation.
* Loan marketplace.

---

## 34. Risks and Mitigation

### 34.1 User Malas Input Transaksi

Risk:

* Finance app gagal jika user tidak konsisten input.

Mitigation:

* Quick add < 10 detik.
* Recurring transactions.
* OCR receipt.
* Widget/shortcut.
* Reminder input harian.

### 34.2 Subscription Tidak Menarik

Risk:

* User merasa finance app harus gratis.

Mitigation:

* Premium fokus outcome: tidak lupa bayar, family budget, backup, report.
* Annual discount.
* Family plan.
* Free plan tetap useful.

### 34.3 Data Loss

Risk:

* Local-first bisa hilang saat device rusak.

Mitigation:

* Export/import.
* Encrypted backup premium.
* Clear education.

### 34.4 App Terlalu Kompleks

Risk:

* Banyak fitur membuat UX berat.

Mitigation:

* Progressive disclosure.
* Start simple.
* Hide advanced features until needed.
* Use templates.

### 34.5 Family Privacy Issue

Risk:

* User takut semua transaksi dilihat pasangan/keluarga.

Mitigation:

* Personal vs shared wallet separation.
* Permission roles.
* Clear visibility controls.

### 34.6 Notification Fatigue

Risk:

* User disable notification jika terlalu banyak.

Mitigation:

* Notification preference.
* Smart frequency.
* Quiet hours.
* Only actionable notifications.

---

## 35. Competitive Positioning

### 35.1 Positioning Statement

Untuk pekerja, pasangan, dan keluarga muda yang ingin mengatur uang tanpa spreadsheet rumit, SpendWealthy adalah aplikasi personal finance offline-first yang menggabungkan pencatatan transaksi, budget, reminder tagihan, dan family budget dalam satu aplikasi aman dan mudah dipakai.

### 35.2 Differentiators

* Fokus pada tagihan dan family budget.
* Offline-first/private.
* Bahasa Indonesia dan kategori lokal.
* UX sederhana.
* Document vault untuk bukti bayar.
* Safe-to-spend insight.

### 35.3 Avoid Competing Directly On

* Bank integration pada awal.
* Investment tracking.
* Accounting kompleks.
* Crypto/portfolio.

---

## 36. AI and Automation Strategy

### 36.1 AI Features Future

* Auto categorize transaction from note/merchant.
* OCR receipt extraction.
* Monthly insight generation.
* Budget recommendation.
* Bill prediction from recurring transactions.
* Ask finance assistant:

  * “Bulan ini uang saya habis di mana?”
  * “Budget mana yang paling sering bocor?”

### 36.2 AI Safety

* Jangan memberi rekomendasi investasi spesifik.
* Jangan memberi nasihat finansial yang terlalu absolut.
* Gunakan disclaimer untuk insight.
* Fokus pada data user dan edukasi umum.

### 36.3 AI Privacy

* Jika AI cloud digunakan, user harus opt-in.
* Jangan kirim data mentah tanpa persetujuan.
* Sediakan local-only mode.
* Masking amount/note jika memungkinkan.

---

## 37. Backup and Sync Strategy

### 37.1 MVP Backup

* Manual export encrypted file.
* Manual import file.
* User set backup password.
* File bisa disimpan user ke Drive/iCloud manual.

### 37.2 Premium Backup

* Automatic encrypted backup.
* Daily/weekly backup.
* Restore on new device.
* Backup status indicator.

### 37.3 Future Sync

* Change log sync.
* Conflict resolution.
* Family collaboration.
* Multi-device support.

### 37.4 Conflict Resolution Principles

* Last write wins for simple metadata.
* Transaction delete uses tombstone.
* Money-impacting conflict should preserve both changes and require review.
* Activity log records changes.

---

## 38. Implementation Roadmap by Sprint

### Sprint 1 — Foundation

* KMP project setup.
* Shared module structure.
* Navigation skeleton.
* Local database setup.
* Money/date utilities.
* Basic design system.

### Sprint 2 — Wallet and Category

* Wallet CRUD.
* Category default seed.
* Settings for wallet/category.
* Unit tests for wallet.

### Sprint 3 — Transaction

* Add transaction.
* Transaction list.
* Edit/delete transaction.
* Wallet balance recalculation.
* Transaction filters.

### Sprint 4 — Budget

* Create monthly budget.
* Category budget.
* Budget progress.
* Budget warning.
* Dashboard budget card.

### Sprint 5 — Bill Reminder

* Add bill reminder.
* Bill instance generation.
* Local notification adapter.
* Mark as paid.
* Overdue status.

### Sprint 6 — Dashboard and Report

* Home dashboard.
* Safe to spend v1.
* Monthly report.
* Category breakdown.
* Recent transactions.

### Sprint 7 — Security and Export

* Biometric lock.
* CSV export.
* PDF export basic.
* Data delete/export settings.

### Sprint 8 — Subscription

* Entitlement model.
* Paywall.
* Play Billing.
* StoreKit.
* Restore purchase.
* Premium gates.

### Sprint 9 — Document Vault

* Upload document.
* Attach to bill/transaction.
* Secure access.
* Search document.

### Sprint 10 — Beta Polish

* Usability fixes.
* Crash monitoring.
* Analytics.
* Empty states.
* Performance optimization.
* Beta release.

---

## 39. Acceptance Criteria Examples

### 39.1 Add Transaction

Given user has a wallet
When user adds expense Rp50.000 from wallet Cash
Then transaction is saved
And wallet balance decreases by Rp50.000
And budget category usage increases if category has budget
And transaction appears in recent transaction list

### 39.2 Mark Bill Paid

Given user has unpaid bill “Internet” due today
When user marks bill as paid using wallet BCA
Then bill status becomes Paid
And expense transaction is created
And wallet balance decreases
And next recurring bill is generated if monthly recurrence is active

### 39.3 Free Limit

Given user is on free plan and already has 3 bill reminders
When user tries to create another bill reminder
Then app shows premium paywall
And no new bill reminder is created until user upgrades

### 39.4 Subscription Expired

Given user previously had premium and created 10 wallets
When subscription expires
Then user can still view existing wallets
But cannot create a new wallet above free limit
And app shows upgrade CTA

---

## 40. Open Questions

### Product

1. Apakah MVP harus langsung include Family Budget, atau ditunda sampai Premium v2?
2. Apakah app harus 100% offline-first atau dari awal ada account?
3. Apakah document vault masuk MVP atau premium launch?
4. Apakah OCR wajib saat launch?
5. Apakah target awal personal user atau family user?

### Technical

1. Akan pakai Compose Multiplatform untuk iOS juga atau SwiftUI native?
2. Local DB pakai SQLDelight atau alternatif lain?
3. Perlu database encryption sejak MVP?
4. Apakah backup pakai custom backend, iCloud/Drive, atau manual encrypted file?
5. Apakah subscription validation cukup client-side untuk MVP atau perlu backend minimal?

### Business

1. Harga ideal untuk Indonesia berapa?
2. Free limit terbaik agar tidak terlalu pelit tapi tetap mendorong upgrade?
3. Trial 7 hari atau 14 hari?
4. Annual discount berapa?
5. Family plan maksimal berapa member?

---

## 41. Recommendation Final

### 41.1 MVP yang Paling Realistis

Bangun dulu:

1. Personal wallet.
2. Transaction tracking.
3. Monthly budget.
4. Bill reminder.
5. Local notification.
6. Basic report.
7. Subscription gates.

Jangan langsung terlalu berat dengan real-time family sync. Family budget bisa dimulai sebagai premium phase setelah core habit terbukti.

### 41.2 Strongest Premium Hook

Premium terbaik:

1. Unlimited bill reminders.
2. Advanced report.
3. PDF export.
4. Document vault.
5. Family budget.
6. Encrypted backup.

### 41.3 Best Product Positioning

> “Aplikasi pengatur uang dan tagihan keluarga yang simpel, private, dan aman.”

### 41.4 Best Technical Direction

* KMP shared domain/data/usecase.
* SQLDelight local database.
* Platform-specific notification, biometric, billing.
* Offline-first MVP.
* Backend ditunda sampai backup/family sync benar-benar dibutuhkan.

### 41.5 Suggested First Build Order

1. Data model.
2. Wallet.
3. Transaction.
4. Budget.
5. Bill reminder.
6. Dashboard.
7. Report.
8. Subscription.
9. Document vault.
10. Family budget.

---

## 42. Appendix — Initial Default Categories

Income:

* Gaji.
* Bonus.
* Freelance.
* Hadiah.
* Investasi.
* Lainnya.

Expense:

* Makan.
* Transport.
* Belanja bulanan.
* Tagihan.
* Cicilan.
* Kesehatan.
* Anak.
* Pendidikan.
* Hiburan.
* Subscription.
* Rumah.
* Donasi.
* Tabungan.
* Lainnya.

Bill templates:

* Internet.
* Listrik.
* Air.
* Sewa rumah.
* Cicilan rumah.
* Cicilan kendaraan.
* Kartu kredit.
* Paylater.
* Asuransi.
* Sekolah.
* Streaming.
* Software.
* Gym.
* Lainnya.

---

## 43. Appendix — Sample Dashboard Copy

Header:

> “Halo, Raka”

Balance:

> “Saldo total”

Safe to spend:

> “Aman dipakai sampai akhir bulan”

Budget:

> “Budget bulan ini”

Bill:

> “Tagihan terdekat”

Insight:

> “Pengeluaran makan kamu naik dibanding bulan lalu.”

CTA:

> “Tambah transaksi”
> “Tambah tagihan”
> “Lihat laporan”

---

## 44. Appendix — Product Principles

1. Input cepat lebih penting daripada form lengkap.
2. Insight harus bisa langsung ditindaklanjuti.
3. User harus merasa aman, bukan diawasi.
4. Free plan harus berguna.
5. Premium harus memberikan ketenangan dan penghematan waktu.
6. Family feature harus menghormati privacy personal.
7. Data user adalah milik user.
8. Offline-first adalah keunggulan, bukan keterbatasan.
9. Jangan membangun fitur investasi sebelum core finance habit kuat.
10. Jangan membuat app terasa seperti spreadsheet.
