# Application dashboard

MODE: Operate

PRODUCT: Apartment Maintenance Complaint Tracker. Primary user: a resident following maintenance at home; Society Admins operate within their assigned society; Super Admins manage the platform.

## Direction: Resident maintenance wayfinding

WORLD: Warm civic wayfinding translated into a residential-care interface. The user-pinned clay and forest palette sits on a paper-warm ground, with one clear humanist sans, precise list density, and custom route marks. No airport-yellow palette, faux maps, decorative paper textures, or default icon grid.

FIRST VIEWPORT: A forest header establishes the product and current account. A compact left rail links to the signed-in role's existing work areas. The page title identifies the resident's home or the admin's scope. Existing complaint, resident, transfer, and society controls remain in their familiar content region, using live API data only.

VISITOR PATH: Residents move from report to request history or transfer request. Society Admins move between unit/resident operations and maintenance requests. Super Admins move between society management and categories. Signed-out visitors move between sign-in and resident access request. Rail items are in-page links to existing sections.

SIGNATURE INTERACTION: Status history uses a connected wayfinding line; each actual audit event becomes a stop, the current/latest event is filled in forest, and event notes stay aligned to their stop. The line never implies status events that were not returned by the API.

CROSS-SURFACE REACH: The same small route marker, warm neutral surfaces, rectangular status labels, and accessible form controls apply to auth, societies, categories, resident operations, transfers, and complaints.

HONEST RISK: Wayfinding language can become decorative. Keep all navigation standard, labels literal, and the signature path attached only to real complaint history.

PRESERVE: No API calls, auth behavior, tenant checks, validation, status rules, form data, or workflow changes. No fabricated metrics or unsupported screens.

SEED REFERENCE: seed:af0a1a2285c55ac5 (user-pinned palette and product task govern over the generated assignment).
