export function downloadWithForm(url: string, params: Record<string, string>, method: 'GET' | 'POST' = 'POST') {
  const form = document.createElement('form');
  form.method = method;
  form.action = url;
  form.style.display = 'none';
  form.target = '_blank';
  Object.entries(params).forEach(([key, value]) => {
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = key;
    input.value = value;
    form.appendChild(input);
  });
  document.body.appendChild(form);
  form.submit();
  document.body.removeChild(form);
}
