import Image from "next/image";
import { fetchSmartDisplayData } from '@/lib/api';
import SmartDisplay from '@/components/SmartDisplay';

type SearchParams = Promise<{ [key: string]: string | string[] | undefined }>

export default async function Page({
  searchParams,
}: {
  searchParams: SearchParams
}) {
  const resolvedParams = await searchParams;
  const mode = typeof resolvedParams.mode === 'string' ? resolvedParams.mode : 'color';
  // User ID from URL or default to the known working setup
  const user = typeof resolvedParams.id === 'string' ? resolvedParams.id : '65a75e2e';

  const data = await fetchSmartDisplayData(user);

  if (!data) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-red-50 text-red-900">
        <h1 className="text-2xl font-bold">Failed to load Infoskjerm Data</h1>
      </div>
    );
  }

  return <SmartDisplay data={data} mode={mode} />;
}
